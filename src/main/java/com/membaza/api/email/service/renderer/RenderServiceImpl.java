package com.membaza.api.email.service.renderer;

import com.membaza.api.email.email.Model;
import com.membaza.api.email.email.ModelType;
import com.membaza.api.email.service.renderer.view.ButtonView;
import com.membaza.api.email.service.renderer.view.TextView;
import com.membaza.api.email.service.renderer.view.View;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import static com.membaza.api.email.email.ModelType.BUTTON;
import static com.membaza.api.email.email.ModelType.TEXT;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
@Service
public final class RenderServiceImpl implements RenderService {

    private final Map<ModelType, View<?>> views;

    RenderServiceImpl() {
        views = new EnumMap<>(ModelType.class);
        views.put(BUTTON, new ButtonView());
        views.put(TEXT, new TextView());
    }

    @Override
    public String render(Model model, UnaryOperator<String> argumentResolver) {
        return viewFor(model).render(model, argumentResolver, this);
    }

    @SuppressWarnings("unchecked")
    private <BOX extends Model> View<BOX> viewFor(Model model) {
        return (View<BOX>) views.get(model.getType());
    }
}
