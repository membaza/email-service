package com.membaza.api.email.controller;

import com.membaza.api.email.persistence.Email;
import com.membaza.api.email.persistence.Template;
import lombok.Data;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.membaza.api.email.persistence.EmailStatus.*;
import static com.membaza.api.email.util.DateUtil.now;
import static com.membaza.api.email.util.ResponseEntityUtil.createdWithId;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
@RestController
@RequestMapping("/email/send")
public final class SendController {

    private final MongoTemplate mongo;
    private final TemplateController templates;

    public SendController(MongoTemplate mongo, TemplateController templates) {
        this.mongo     = requireNonNull(mongo);
        this.templates = requireNonNull(templates);
    }

    @PostMapping
    ResponseEntity<Void> sendEmail(@RequestBody PostedEmail posted) {
        if (posted.getRecipients().isEmpty()) {
            throw new IllegalArgumentException(
                "At least one recipient must be specified."
            );
        }

        // Throws an exception if no such template
        final ResponseEntity<Template> res = templates.getTemplateByName(posted.getTemplate());
        if (!res.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException(
                "Specified template '" + posted.getTemplate() + "' do not exist."
            );
        }

        final Email email = new Email();
        email.setTimestamp(now());
        email.setStatus(CREATED);
        email.setTemplate(posted.getTemplate());
        email.setRecipients(posted.getRecipients());
        email.setArgs(ofNullable(posted.getArgs()).orElseGet(HashMap::new));

        mongo.insert(email);
        return createdWithId(email.getId());
    }

    @GetMapping("/{id}")
    ResponseEntity<Email> getEmailById(@PathVariable String id) {
        final Email email = mongo.findOne(query(where("id").is(id)), Email.class);
        return email == null ? notFound().build() : ok(email);
    }

    @GetMapping("/created")
    List<Email> getEmailsCreated() {
        return mongo.find(query(where("status").is(CREATED)), Email.class);
    }

    @GetMapping("/processing")
    List<Email> getEmailsProcessing() {
        return mongo.find(query(where("status").is(PROCESSING)), Email.class);
    }

    @GetMapping("/success")
    List<Email> getEmailsSuccess() {
        return mongo.find(query(where("status").is(SUCCESS)), Email.class);
    }

    @GetMapping("/failure")
    List<Email> getEmailsFailure() {
        return mongo.find(query(where("status").is(FAILURE)), Email.class);
    }

    @GetMapping
    List<Email> getAllEmails() {
        return mongo.findAll(Email.class);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = BAD_REQUEST)
    public void illegalArgumentException() {}

    @Data
    private static final class PostedEmail {
        private @NotNull String template;
        private @NotNull List<Email> recipients;
        private Map<String, String> args;
    }
}
