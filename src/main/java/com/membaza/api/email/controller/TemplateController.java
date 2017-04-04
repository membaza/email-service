package com.membaza.api.email.controller;

import com.membaza.api.email.persistence.Template;
import com.mongodb.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.http.HttpStatus.CONFLICT;

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
    void createTemplate(@RequestBody Template template) {
        mongo.insert(template);
    }

    @GetMapping
    Template getTemplateByName(String name) {
        return mongo.findOne(query(where("name").is(name)), Template.class);
    }

    @GetMapping
    List<Template> getTemplates() {
        return mongo.findAll(Template.class);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(value = CONFLICT, reason = "A template with that name already exists")
    public void duplicateKeyException() {}
}