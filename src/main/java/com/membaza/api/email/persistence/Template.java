package com.membaza.api.email.persistence;

import com.membaza.api.email.email.Box;
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
public final class Template {

    private @Id String id;

    @Indexed(unique = true)
    private @NotNull String name;

    private @NotNull EmailAddress sender;
    private EmailAddress replyTo;

    private List<Map<String, Map<String, String>>> languages;
    private List<Box> content;

}