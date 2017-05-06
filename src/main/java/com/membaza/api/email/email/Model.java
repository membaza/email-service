package com.membaza.api.email.email;

import com.membaza.api.email.service.renderer.view.View;

/**
 * Model for a component that can be part of an email. For every {@code Model}
 * there is also a {@link View}.
 *
 * @author Emil Forslund
 * @since 1.0.0
 */
public interface Model {

    ModelType getType();

}
