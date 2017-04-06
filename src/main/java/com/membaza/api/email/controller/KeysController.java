package com.membaza.api.email.controller;

import com.membaza.api.email.persistence.ApiKey;
import com.membaza.api.email.persistence.Privilege;
import com.mongodb.DuplicateKeyException;
import lombok.Data;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.membaza.api.email.util.ControllerUtil.byName;
import static com.membaza.api.email.util.ControllerUtil.byUsername;
import static com.membaza.api.email.util.ResponseEntityUtil.created;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@RestController
@RequestMapping("/email/keys")
public final class KeysController {

    private final Environment env;
    private final MongoTemplate mongo;
    private final PasswordEncoder encoder;

    public KeysController(Environment env,
                          MongoTemplate mongo,
                          PasswordEncoder encoder) {

        this.env     = requireNonNull(env);
        this.mongo   = requireNonNull(mongo);
        this.encoder = requireNonNull(encoder);
    }

    @PostMapping
    public ResponseEntity<Void> createApiKey(@RequestBody PostedKey posted) {
        final String username = posted.getUsername();
        final String password = posted.getPassword();

        if (username == null || "".equals(username)) {
            throw new IllegalArgumentException("An username must be specified");
        }

        if (password == null || "".equals(password)) {
            throw new IllegalArgumentException("A password must be specified");
        }

        final ApiKey key = new ApiKey();
        key.setUsername(posted.getUsername());
        key.setPassword(encoder.encode(password));
        key.setAuthorities(ofNullable(posted.getAuthorities()).orElseGet(this::defaultPrivileges));

        mongo.insert(key);
        return created(key.getUsername());
    }

    @PutMapping("/{username}")
    public ResponseEntity<Void> updateApiKey(@PathVariable String username,
                                             @RequestBody PostedKey posted) {

        final String password = posted.getPassword();

        if (username == null || "".equals(username)) {
            throw new IllegalArgumentException("An username must be specified");
        }

        if (password == null || "".equals(password)) {
            throw new IllegalArgumentException("A password must be specified");
        }

        final ApiKey key = new ApiKey();
        key.setId(findByUsername(username).getId());
        key.setUsername(ofNullable(posted.getUsername()).orElse(username));
        key.setPassword(encoder.encode(posted.getPassword()));
        key.setAuthorities(ofNullable(posted.getAuthorities()).orElseGet(HashSet::new));
        mongo.save(key);

        return accepted().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteApiKey(@PathVariable String username) {
        if (!mongo.remove(byUsername(username), ApiKey.class).isUpdateOfExisting()) {
            return notFound().build();
        } else {
            return noContent().build();
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiKey> getApiKeyByName(@PathVariable String name) {
        final ApiKey key = mongo.findOne(byUsername(name), ApiKey.class);
        return key == null ? notFound().build() : ok(key);
    }

    @GetMapping
    public List<ApiKey> getApiKeys() {
        return mongo.findAll(ApiKey.class);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(value = CONFLICT, reason = "The specified key already exists")
    public void duplicateKeyException() {}

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(value = NOT_FOUND, reason = "Could not find any apiKey with that username.")
    public void nullPointerException() {}

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = BAD_REQUEST)
    public void illegalArgumentException() {}

    @Data
    public static class PostedKey {
        private String username;
        private String password;
        private Set<Privilege> authorities;
    }

    private Set<Privilege> defaultPrivileges() {
        return Stream.of(env.getProperty("service.privileges.default").split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(Privilege::valueOf)
            .collect(toSet());
    }

    private ApiKey findByUsername(String username) {
        return mongo.findOne(byName(username), ApiKey.class);
    }
}