package com.membaza.api.email.util;

import org.springframework.data.mongodb.core.query.Query;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class ControllerUtil {

    public static Query byName(String name) {
        return query(where("name").is(name));
    }

    private ControllerUtil() {}
}
