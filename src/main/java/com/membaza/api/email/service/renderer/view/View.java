package com.membaza.api.email.service.renderer.view;

import com.membaza.api.email.email.Model;
import com.membaza.api.email.service.renderer.RenderService;

import java.util.function.UnaryOperator;

/**
 * Interface describing a view of a {@link Model} that can be used to render the
 * model as HTML in an email.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public interface View<M extends Model> {

    /**
     * Renders the specified {@link Model} to HTML, using the specified method
     * reference to convert any arguments into translated text and the specified
     * renderer to render any nested models.
     *
     * @param model             the model to render
     * @param argumentResolver  resolver for any arguments
     * @param renderer          the renderer
     * @return                  the rendered HTML code
     */
    String render(M model,
                  UnaryOperator<String> argumentResolver,
                  RenderService renderer);

}