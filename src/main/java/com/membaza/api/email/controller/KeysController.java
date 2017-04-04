package com.membaza.api.email.controller;

import com.membaza.api.email.persistence.ApiKey;
import com.membaza.api.email.persistence.Privilege;
import com.membaza.api.email.service.random.RandomService;
import com.mongodb.DuplicateKeyException;
import lombok.Data;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@RestController
@RequestMapping("/email/keys")
public final class KeysController {

    private final MongoTemplate mongo;
    private final PasswordEncoder encoder;
    private final RandomService random;

    public KeysController(MongoTemplate mongo,
                          PasswordEncoder encoder,
                          RandomService random) {

        this.mongo   = requireNonNull(mongo);
        this.encoder = requireNonNull(encoder);
        this.random  = requireNonNull(random);
    }

    @PostMapping
    public String createApiKey(@RequestBody PostedKey posted) {
        final String username = posted.getUsername();
        String password = posted.getPassword();

        if (username == null || "".equals(username)) {
            throw new IllegalArgumentException("An username must be specified");
        }

        if (password == null) {
            password = random.nextString(40);
        }

        final ApiKey key = new ApiKey();
        key.setUsername(posted.getUsername());
        key.setPassword(encoder.encode(password));
        key.setAuthorities(posted.getAuthorities());

        mongo.insert(key);
        return password;
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(value = CONFLICT, reason = "The specified key already exists")
    public void duplicateKeyException() {}

    @Data
    public static class PostedKey {
        private @NotNull String username;
        private String password;
        private @NotNull Set<Privilege> authorities;
    }
}