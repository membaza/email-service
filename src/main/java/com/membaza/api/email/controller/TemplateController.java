package com.membaza.api.email.controller;

import com.membaza.api.email.persistence.Template;
import com.mongodb.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.membaza.api.email.util.ResponseEntityUtil.created;
import static java.util.Objects.requireNonNull;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.ResponseEntity.*;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@RestController
@RequestMapping("/email/templates")
public final class TemplateController {

    private final MongoTemplate mongo;

    public TemplateController(MongoTemplate mongo) {
        this.mongo = requireNonNull(mongo);
    }

    @PostMapping
    ResponseEntity<Void> createTemplate(@RequestBody Template template) {
        mongo.insert(template);
        return created(template.getName());
    }

    @PutMapping("/{name}")
    ResponseEntity<Void> updateTemplate(@PathVariable String name,
                        @RequestBody Template template) {

        final String id = mongo.findOne(
            query(where("name").is(name)),
            Template.class
        ).getId();

        template.setId(id);
        mongo.save(template);
        return accepted().build();
    }

    @DeleteMapping("/{name}")
    ResponseEntity<Void> deleteTemplate(@PathVariable String name) {
        if (!mongo.remove(query(where("name").is(name)), Template.class).isUpdateOfExisting()) {
            return notFound().build();
        } else {
            return noContent().build();
        }
    }

    @GetMapping("/{name}")
    ResponseEntity<Template> getTemplateByName(@PathVariable String name) {
        final Template template = findByName(name);
        if (template == null) {
            return notFound().build();
        } else {
            return ok(template);
        }
    }

    @GetMapping
    List<Template> getTemplates() {
        return mongo.findAll(Template.class);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(value = CONFLICT, reason = "A template with that name already exists")
    public void duplicateKeyException() {}

    private Template findByName(String name) {
        return mongo.findOne(query(where("name").is(name)), Template.class);
    }
}