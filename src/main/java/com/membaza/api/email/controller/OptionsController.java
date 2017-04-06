package com.membaza.api.email.controller;

import com.membaza.api.email.persistence.Option;
import com.mongodb.DuplicateKeyException;
import lombok.Data;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.membaza.api.email.util.ControllerUtil.byKey;
import static com.membaza.api.email.util.ResponseEntityUtil.created;
import static java.util.Objects.requireNonNull;
import static org.springframework.data.mongodb.core.query.Update.update;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
@RestController
@RequestMapping("/email/options")
public final class OptionsController {

    private final MongoTemplate mongo;

    public OptionsController(MongoTemplate mongo) {
        this.mongo = requireNonNull(mongo);
    }

    @PostMapping
    ResponseEntity<Void> createOption(@RequestBody PostedOption posted) {
        final String key = posted.getKey();
        if (key == null) {
            throw new IllegalArgumentException("No key specified.");
        }

        final Option option = new Option();
        option.setKey(key);
        option.setValue(posted.getValue());
        mongo.save(option);
        return created(option.getKey());
    }

    @PutMapping("/{key}")
    ResponseEntity<Void> updateOption(@PathVariable String key,
                                      @RequestBody PostedOption option) {
        if (mongo.updateFirst(byKey(key), update("value", option.getValue()), Option.class)
                .isUpdateOfExisting()) {
            return accepted().build();
        } else {
            return notFound().build();
        }
    }

    @DeleteMapping("/{key}")
    ResponseEntity<Void> deleteOption(@PathVariable String key) {
        if (mongo.remove(byKey(key), Option.class).isUpdateOfExisting()) {
            return noContent().build();
        } else {
            return notFound().build();
        }
    }

    @GetMapping("/{key}")
    ResponseEntity<Option> getOptionByKey(@PathVariable String key) {
        final Option option = mongo.findOne(byKey(key), Option.class);
        return option == null ? notFound().build() : ok(option);
    }

    @GetMapping
    List<Option> getOptions() {
        return mongo.findAll(Option.class);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(value = CONFLICT, reason = "An option with that key already exists")
    public void duplicateKeyException() {}

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = BAD_REQUEST)
    public void illegalArgumentException() {}

    @Data
    private static final class PostedOption {
        private String key;
        private @NotNull String value;
    }
}
