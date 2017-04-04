package com.membaza.api.email.service.random;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
public interface RandomService {

    long nextLong();

    String nextString(int length);

}