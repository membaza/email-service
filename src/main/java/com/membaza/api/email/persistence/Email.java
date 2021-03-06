package com.membaza.api.email.persistence;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Email that is either about to be sent or that has already been sent and now
 * exist for statistical or debugging purposes.
 *
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
    private @NotNull String language;
    private @NotNull List<EmailAddress> recipients;
    private @NotNull Map<String, String> args;
    private String error;

}