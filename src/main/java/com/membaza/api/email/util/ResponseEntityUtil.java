package com.membaza.api.email.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class ResponseEntityUtil {

    public static ResponseEntity<Void> createdWithName(String name) {
        return createdWith("name", name);
    }

    public static ResponseEntity<Void> createdWithId(String id) {
        return createdWith("id", id);
    }

    private static ResponseEntity<Void> createdWith(String key, String value) {
        final URI location = ServletUriComponentsBuilder
            .fromCurrentServletMapping().path("/{" + key + "}").build()
            .expand(value).toUri();

        return ResponseEntity.created(location).build();
    }

    private ResponseEntityUtil() {}

}
