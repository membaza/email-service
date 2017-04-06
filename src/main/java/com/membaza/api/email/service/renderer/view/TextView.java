package com.membaza.api.email.service.renderer.view;

import com.membaza.api.email.email.box.Text;
import com.membaza.api.email.service.renderer.RenderService;

import java.util.function.UnaryOperator;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class TextView implements View<Text> {

    @Override
    public String render(Text model, UnaryOperator<String> argumentResolver, RenderService renderer) {
        return "<p>" + argumentResolver.apply(model.getValue()) + "</p>";
    }
}
