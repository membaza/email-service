package com.membaza.api.email.persistence;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@Data
@Document(collection = "emails")
public final class Email {

    private @Id String id;
    private @NotNull Date timestamp;
    private @NotNull EmailStatus status;
    private @NotNull String template;
    private @NotNull List<Email> recipients;
    private @NotNull Map<String, String> args;

}