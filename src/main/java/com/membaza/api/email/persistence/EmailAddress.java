package com.membaza.api.email.persistence;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * An email address in the system that can be the target for an email. Contains
 * information about the name of the contact as well as the preferred language.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
@Data
public final class EmailAddress {

    private @NotNull String name;
    private @NotNull String address;
    private @NotNull String language;

}