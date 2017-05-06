package com.membaza.api.email.persistence;

import com.membaza.api.email.service.SenderService;

/**
 * The current status of an email.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public enum EmailStatus {

    /**
     * The email has been created, but no action to actually send it has been
     * taken yet. The email is "up for grabs" for any {@link SenderService}.
     */
    CREATED,

    /**
     * The email is being processed by a particular {@link SenderService}. It
     * should be expected to change status again very soon.
     */
    PROCESSING,

    /**
     * The email has been sent. The database record only exists for statistical
     * reasons beyond this point and can be removed if necessary.
     */
    SUCCESS,

    /**
     * The email could not be delivered. A detailed message has been stored in
     * the database entity describing the problem. The record only exists for
     * statistical and debugging reasons beyond this point and can be removed if
     * necessary. No retry will be attempted automatically.
     */
    FAILURE

}
