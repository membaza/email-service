package com.membaza.api.email.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Utility class for creating {@link ResponseEntity response entities} with
 * links to particular resources.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class ResponseEntityUtil {

    /**
     * Creates an {@link ResponseEntity} with a link to the current servlet
     * mapping with the specified name as the path variable.
     *
     * @param name  the path variable
     * @return      the response entity
     */
    public static ResponseEntity<Void> createdWithName(String name) {
        return createdWith("name", name);
    }

    /**
     * Creates an {@link ResponseEntity} with a link to the current servlet
     * mapping with the specified id as the path variable.
     *
     * @param id  the path variable
     * @return    the response entity
     */
    public static ResponseEntity<Void> createdWithId(String id) {
        return createdWith("id", id);
    }

    /**
     * Creates an {@link ResponseEntity} with a link to the current servlet
     * mapping with the specified key and value as the path variable.
     *
     * @param key    the path variable key
     * @param value  the path variable value
     * @return       the response entity
     */
    private static ResponseEntity<Void> createdWith(String key, String value) {
        final URI location = ServletUriComponentsBuilder
            .fromCurrentServletMapping().path("/{" + key + "}").build()
            .expand(value).toUri();

        return ResponseEntity.created(location).build();
    }

    /**
     * Utility classes should not be instantiated.
     */
    private ResponseEntityUtil() {}

}