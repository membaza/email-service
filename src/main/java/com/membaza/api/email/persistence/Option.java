package com.membaza.api.email.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@Data
@Document(collection = "options")
@JsonIgnoreProperties("id")
public final class Option {

    private @Id String id;
    private @Indexed(unique=true) String name;
    private @NotNull String value;

}