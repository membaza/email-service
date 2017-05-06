package com.membaza.api.email.util;

import org.springframework.data.mongodb.core.query.Query;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Utility class for simplifying controllers by statically importing common
 * logic.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class ControllerUtil {

    /**
     * Returns a {@link Query} that matches records where the "name" field is
     * equal to the specified name.
     *
     * @param name  the name
     * @return      the query
     */
    public static Query byName(String name) {
        return query(where("name").is(name));
    }

    /**
     * Utility classes should not be instantiated.
     */
    private ControllerUtil() {}
}
