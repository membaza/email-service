package com.membaza.api.email.util;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utility class for generating dates in the UTC timezone.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class DateUtil {

    /**
     * Returns the current date in UTC.
     *
     * @return  the current date
     */
    public static Date now() {
        return Date.from(Instant.now(Clock.system(ZoneId.of("UTC"))));
    }

    /**
     * Utility classes should not be instantiated.
     */
    private DateUtil() {}
}
