package com.membaza.api.email.persistence;

import lombok.Data;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
@Data
public final class EmailAddress {

    private String name;
    private String address;
    private String language;

}
