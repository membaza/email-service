package com.membaza.api.email.controller;

import com.membaza.api.email.persistence.Template;
import com.mongodb.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.membaza.api.email.util.ControllerUtil.byName;
import static com.membaza.api.email.util.ResponseEntityUtil.createdWithName;
import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.*;

/**
 * Controller for the {@code /email/templates} endpoints.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
@RestController
@RequestMapping("/email/templates")
public final class TemplateController {

    private final MongoTemplate mongo;

    TemplateController(MongoTemplate mongo) {
        this.mongo = requireNonNull(mongo);
    }

    @PostMapping
    ResponseEntity<Void> createTemplate(@RequestBody Template template) {
        mongo.insert(template);
        return createdWithName(template.getName());
    }

    @PutMapping("/{name}")
    ResponseEntity<Void> updateTemplate(@PathVariable String name,
                        @RequestBody Template template) {
        template.setId(findByName(name).getId());
        mongo.save(template);
        return accepted().build();
    }

    @DeleteMapping("/{name}")
    ResponseEntity<Void> deleteTemplate(@PathVariable String name) {
        if (!mongo.remove(byName(name), Template.class).isUpdateOfExisting()) {
            return notFound().build();
        } else {
            return noContent().build();
        }
    }

    @GetMapping("/{name}")
    ResponseEntity<Template> getTemplateByName(@PathVariable String name) {
        final Template template = findByName(name);
        return template == null ? notFound().build() : ok(template);
    }

    @GetMapping
    List<Template> getTemplates() {
        return mongo.findAll(Template.class);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(value = CONFLICT, reason = "A template with that name already exists")
    void duplicateKeyException() {}

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(value = NOT_FOUND, reason = "Could not find any template with that name.")
    void nullPointerException() {}

    private Template findByName(String name) {
        return mongo.findOne(byName(name), Template.class);
    }
}