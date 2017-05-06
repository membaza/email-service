package com.membaza.api.email.service.renderer.view;

import com.membaza.api.email.email.Model;
import com.membaza.api.email.service.renderer.RenderService;

import java.util.function.UnaryOperator;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
public interface View<M extends Model> {

    String render(M model,
                  UnaryOperator<String> argumentResolver,
                  RenderService renderer);

}
