package com.membaza.api.email.service.random;

/**
 * Service used to generate secure random values and strings for production
 * applications and predictable pseudo-random number sequences for testing by
 * simply plugging in different implementations.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public interface RandomService {

    /**
     * Returns the next pseudo-random long value in the sequence. The value can
     * be any 64-bit value, both positive and negative.
     *
     * @return  the next random long
     */
    long nextLong();

    /**
     * Returns the next pseudo-random string of exactly the specified length.
     * The string might contain alphanumerical characters as well as dash (-)
     * and underscore (_).
     *
     * @param length  the length of the string
     * @return        the string
     */
    String nextString(int length);

}