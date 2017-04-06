package com.membaza.api.email.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
public final class ResponseEntityUtil {

    public static ResponseEntity<Void> created(String name) {
        final URI location = ServletUriComponentsBuilder
            .fromCurrentServletMapping().path("/{name}").build()
            .expand(name).toUri();

        return ResponseEntity.created(location).build();
    }

    private ResponseEntityUtil() {}

}
