package com.membaza.api.email.service.renderer.view;

import com.membaza.api.email.email.model.Button;
import com.membaza.api.email.service.renderer.RenderService;

import java.util.function.UnaryOperator;

/**
 * {@link View} for the {@link Button} model.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class ButtonView implements View<Button> {

    @Override
    public String render(Button model,
                         UnaryOperator<String> argRes,
                         RenderService renderer) {

        return
        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"btn btn-primary\">\n" +
        "  <tbody>\n" +
        "    <tr>\n" +
        "      <td align=\"left\">\n" +
        "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
        "          <tbody>\n" +
        "            <tr>\n" +
        "              <td> <a href=\"" +
            argRes.apply(model.getHref()) + "\" target=\"_blank\">" +
            argRes.apply(model.getValue()) + "</a> </td>\n" +
        "            </tr>\n" +
        "          </tbody>\n" +
        "        </table>\n" +
        "      </td>\n" +
        "    </tr>\n" +
        "  </tbody>\n" +
        "</table>";
    }
}