package com.membaza.api.email.util;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
public final class DateUtil {

    public static Date now() {
        return Date.from(Instant.now(Clock.system(ZoneId.of("UTC"))));
    }

    private DateUtil() {}
}
