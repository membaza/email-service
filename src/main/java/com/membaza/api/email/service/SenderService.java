package com.membaza.api.email.service;

import com.membaza.api.email.persistence.Email;
import com.membaza.api.email.persistence.EmailAddress;
import com.membaza.api.email.persistence.Option;
import com.membaza.api.email.persistence.Template;
import com.membaza.api.email.service.renderer.RenderService;
import com.speedment.common.mapstream.MapStream;
import lombok.Data;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.membaza.api.email.persistence.EmailStatus.*;
import static com.membaza.api.email.util.ControllerUtil.byName;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@Service
@EnableScheduling
public final class SenderService {

    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^$]*)}");
    private static final String BEFORE_FILE = "/html/email_before.html",
                                AFTER_FILE  = "/html/email_after.html";

    private final MongoTemplate mongo;
    private final RenderService renderer;
    private final Environment env;
    private final RestTemplate mailgun;

    public SenderService(MongoTemplate mongo,
                         RenderService renderer,
                         Environment env) {

        this.mongo    = requireNonNull(mongo);
        this.renderer = requireNonNull(renderer);
        this.env      = requireNonNull(env);
        this.mailgun  = new RestTemplate();
    }

    @Scheduled(fixedDelay = 1000) // Every second
    void lookForWork() {
        // Take an email from the queue, setting the status atomically.
        Email email;
        nextEmail: while (null != (email = mongo.findAndModify(
                query(where("status").is(CREATED)),
                update("status", PROCESSING),
                Email.class))) {

            try {
                // Find the correct template.
                final Template template = mongo.findOne(byName(email.getTemplate()), Template.class);
                if (template == null) {
                    email.setStatus(FAILURE);
                    email.setError("Invalid template '" + email.getTemplate() + "'.");
                    mongo.save(email);
                    continue;
                }

                // Translate the template texts into every desired language.
                final Map<String, Map<String, String>> textsByLanguage =
                    MapStream.of(template.getLanguages())
                        .mapValue((Function<Map<String, String>, Map<String, String>>) HashMap::new)
                        .mapValue((lang, texts) -> MapStream.of(texts)
                            .mapValue(text -> resolve(text, lang, email, texts))
                            .toMap()
                        ).toMap();

                // Create an unique text for each user.
                for (final EmailAddress recipient : email.getRecipients()) {
                    final StringJoiner content = new StringJoiner("\n");

                    final String lang = ofNullable(recipient.getLanguage())
                        .orElseGet(() -> email.getLanguage() == null
                            ? defaultLanguage() : email.getLanguage());

                    final Map<String, String> dictionary = textsByLanguage.get(lang);
                    final UnaryOperator<String> resolver = s -> resolve(s, lang, email, dictionary);
                    final String subject = resolver.apply(dictionary.get("subject"));

                    load(BEFORE_FILE).map(resolver).forEachOrdered(content::add);

                    template.getContent().stream()
                        .map(model -> renderer.render(model, resolver))
                        .forEachOrdered(content::add);

                    load(AFTER_FILE).map(resolver).forEachOrdered(content::add);

                    final String html = content.toString();
                    try {
                        send(subject,
                            recipient.getName(),
                            recipient.getAddress(),
                            html
                        );
                    } catch (final MailDeliveryException ex) {
                        email.setStatus(FAILURE);
                        email.setError(ex.getMessage());
                        mongo.save(email);
                        continue nextEmail;
                    }
                }

                // All email has been delivered.
                email.setStatus(SUCCESS);
                email.setLanguage(null);
                email.setArgs(null);
                email.setTemplate(null);
                email.setRecipients(null);
                mongo.save(email);

            } catch (final MalformedEmailException ex) {
                email.setStatus(FAILURE);
                email.setError(ex.getMessage());
                mongo.save(email);
            }
        }
    }

    private String resolve(String text,
                           String lang,
                           Email email,
                           Map<String, String> dictionary) {

        final StringBuilder result = new StringBuilder();
        final Matcher matcher = PATTERN.matcher(text);
        int origin = 0;

        while (matcher.find()) {
            final String key   = matcher.group(1);
            final String value = resolve(find(key, lang, email, dictionary), lang, email, dictionary);

            result.append(text.substring(origin, matcher.start()));
            result.append(value);

            origin = matcher.end();
        }

        if (origin < text.length()) {
            result.append(text.substring(origin));
        }

        return result.toString();
    }

    private String find(String key, String lang, Email email, Map<String, String> dictionary) {
        if ("lang".equals(key)) {
            return lang;
        }

        // If it is available as an email argument, use that.
        final String fromArgs = email.getArgs().get(key);
        if (fromArgs == null) {

            // If it is available in the dictionary, use it.
            final String fromDictionary = dictionary.get(key);
            if (fromDictionary == null) {

                // If it is available in the options, use that.
                final String fromOptions = mongo.findOne(byName(key), Option.class)
                    .getValue();

                if (fromOptions == null) {
                    throw new MalformedEmailException(key);
                }

                return fromOptions;
            } else {
                return fromDictionary;
            }
        } else {
            return fromArgs;
        }
    }

    private String defaultLanguage() {
        return env.getProperty("service.language.default");
    }

    private Stream<String> load(String filename) {
        try {
            return Files.lines(Paths.get(
                new PathMatchingResourcePatternResolver()
                    .getResource(filename)
                    .getURI()
            ));
        } catch (final IOException ex) {
            throw new RuntimeException("Could not find required file '" +
                                           filename + "' on classpath.");
        }
    }

    private void send(String subject, String to, String toEmail, String body) {
        final String url = "https://api.mailgun.net/v3/" +
            env.getProperty("mailgun.domain") + "/messages";

        final MultiValueMap<String, String> args = new LinkedMultiValueMap<>();
        args.put("subject", singletonList(subject));
        args.put("from",  singletonList(env.getProperty("service.email.sitename") +
            " <" +  env.getProperty("service.email.sender") + ">"));
        args.put("to", singletonList(to + " <" + toEmail + ">"));
        args.put("html", singletonList(body));

        final ResponseEntity<MailGunResponse> response =
            mailgun.postForEntity(url, args, MailGunResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new MailDeliveryException(
                "Error delivering mail. Message: " +
                    response.getBody().getMessage()
            );
        }
    }

    private static final class MalformedEmailException extends RuntimeException {
        MalformedEmailException(String arg) {
            super("Invalid argument ${'" + arg + "'}.");
        }
    }

    private static final class MailDeliveryException extends RuntimeException {
        MailDeliveryException(String msg) {
            super(msg);
        }
    }

    @Data
    private final static class MailGunResponse {
        private String message;
        private String id;
    }
}
