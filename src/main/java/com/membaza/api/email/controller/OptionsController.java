package com.membaza.api.email.controller;

import com.membaza.api.email.persistence.Option;
import com.mongodb.DuplicateKeyException;
import lombok.Data;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.membaza.api.email.util.ControllerUtil.byName;
import static com.membaza.api.email.util.ResponseEntityUtil.createdWithName;
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
        final String key = posted.getName();
        if (key == null) {
            throw new IllegalArgumentException("No name specified.");
        }

        final Option option = new Option();
        option.setName(key);
        option.setValue(posted.getValue());
        mongo.save(option);
        return createdWithName(option.getName());
    }

    @PutMapping("/{name}")
    ResponseEntity<Void> updateOption(@PathVariable String key,
                                      @RequestBody PostedOption option) {
        if (mongo.updateFirst(byName(key), update("value", option.getValue()), Option.class)
                .isUpdateOfExisting()) {
            return accepted().build();
        } else {
            return notFound().build();
        }
    }

    @DeleteMapping("/{name}")
    ResponseEntity<Void> deleteOption(@PathVariable String name) {
        if (mongo.remove(byName(name), Option.class).isUpdateOfExisting()) {
            return noContent().build();
        } else {
            return notFound().build();
        }
    }

    @GetMapping("/{name}")
    ResponseEntity<Option> getOptionByName(@PathVariable String name) {
        final Option option = mongo.findOne(byName(name), Option.class);
        return option == null ? notFound().build() : ok(option);
    }

    @GetMapping
    List<Option> getOptions() {
        return mongo.findAll(Option.class);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(value = CONFLICT, reason = "An option with that name already exists")
    public void duplicateKeyException() {}

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = BAD_REQUEST)
    public void illegalArgumentException() {}

    @Data
    private static final class PostedOption {
        private String name;
        private @NotNull String value;
    }
}
