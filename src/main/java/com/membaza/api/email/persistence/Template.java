package com.membaza.api.email.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.membaza.api.email.email.Model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
@Data
@Document(collection = "templates")
@JsonIgnoreProperties("id")
public final class Template {

    private @Id String id;

    @Indexed(unique = true)
    private @NotNull String name;

    private EmailAddress replyTo;
    private @NotNull EmailAddress sender;
    private @NotNull List<Model> content;

    private Map<String, Map<String, String>> languages;
}