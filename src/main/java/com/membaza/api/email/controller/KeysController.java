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
import static com.membaza.api.email.util.ResponseEntityUtil.createdWithName;
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
        final String name     = posted.getName();
        final String password = posted.getPassword();

        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("An name must be specified");
        }

        if (password == null || "".equals(password)) {
            throw new IllegalArgumentException("A password must be specified");
        }

        final ApiKey key = new ApiKey();
        key.setName(posted.getName());
        key.setPassword(encoder.encode(password));
        key.setAuthorities(ofNullable(posted.getAuthorities()).orElseGet(this::defaultPrivileges));

        mongo.insert(key);
        return createdWithName(key.getUsername());
    }

    @PutMapping("/{name}")
    public ResponseEntity<Void> updateApiKey(@PathVariable String name,
                                             @RequestBody PostedKey posted) {

        final String password = posted.getPassword();

        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("An name must be specified");
        }

        if (password == null || "".equals(password)) {
            throw new IllegalArgumentException("A password must be specified");
        }

        final ApiKey key = new ApiKey();
        key.setId(findByName(name).getId());
        key.setName(ofNullable(posted.getName()).orElse(name));
        key.setPassword(encoder.encode(posted.getPassword()));
        key.setAuthorities(ofNullable(posted.getAuthorities()).orElseGet(HashSet::new));
        mongo.insert(key);

        return accepted().build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteApiKey(@PathVariable String username) {
        if (!mongo.remove(byName(username), ApiKey.class).isUpdateOfExisting()) {
            return notFound().build();
        } else {
            return noContent().build();
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiKey> getApiKeyByName(@PathVariable String name) {
        final ApiKey key = findByName(name);
        return key == null ? notFound().build() : ok(key);
    }

    @GetMapping
    public List<ApiKey> getApiKeys() {
        return mongo.findAll(ApiKey.class);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(value = CONFLICT, reason = "The specified name already exists")
    public void duplicateKeyException() {}

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(value = NOT_FOUND, reason = "Could not find any apiKey with that name.")
    public void nullPointerException() {}

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = BAD_REQUEST)
    public void illegalArgumentException() {}

    @Data
    public static class PostedKey {
        private String name;
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

    private ApiKey findByName(String username) {
        return mongo.findOne(byName(username), ApiKey.class);
    }
}