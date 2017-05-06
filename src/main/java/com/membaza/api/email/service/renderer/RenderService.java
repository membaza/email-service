package com.membaza.api.email.service.renderer;

import com.membaza.api.email.email.Model;

import java.util.function.UnaryOperator;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
public interface RenderService {

    String render(Model model, UnaryOperator<String> argumentResolver);

}
