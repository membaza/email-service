package com.membaza.api.email.service.renderer.view;

import com.membaza.api.email.email.model.Text;
import com.membaza.api.email.service.renderer.RenderService;

import java.util.function.UnaryOperator;

/**
 * {@link View} for the {@link Text} model.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class TextView implements View<Text> {

    @Override
    public String render(Text model,
                         UnaryOperator<String> argRes,
                         RenderService renderer) {

        return "<p>" + argRes.apply(model.getValue()) + "</p>";
    }
}
