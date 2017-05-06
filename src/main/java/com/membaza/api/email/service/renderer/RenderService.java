package com.membaza.api.email.service.renderer;

import com.membaza.api.email.email.Model;

import java.util.function.UnaryOperator;

/**
 * Service that renders {@link Model models} into HTML.
 *
 * @author Emil Forslund
 * @since 1.0.0
 */
public interface RenderService {

    /**
     * Renders the specified {@link Model} into HTML, using the specified method
     * reference to convert any arguments into translated text.
     *
     * @param model   the model to render
     * @param argRes  resolves any arguments into translated text
     * @return        the rendered HTML
     */
    String render(Model model, UnaryOperator<String> argRes);

}
