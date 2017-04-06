package com.membaza.api.email.service.renderer;

import com.membaza.api.email.email.Box;
import com.membaza.api.email.email.BoxType;
import com.membaza.api.email.service.renderer.view.ButtonView;
import com.membaza.api.email.service.renderer.view.TextView;
import com.membaza.api.email.service.renderer.view.View;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import static com.membaza.api.email.email.BoxType.BUTTON;
import static com.membaza.api.email.email.BoxType.TEXT;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
@Service
public final class RenderServiceImpl implements RenderService {

    private final Map<BoxType, View<?>> views;

    RenderServiceImpl() {
        views = new EnumMap<>(BoxType.class);
        views.put(BUTTON, new ButtonView());
        views.put(TEXT, new TextView());
    }

    @Override
    public String render(Box box, UnaryOperator<String> argumentResolver) {
        return viewFor(box).render(box, argumentResolver, this);
    }

    @SuppressWarnings("unchecked")
    private <BOX extends Box> View<BOX> viewFor(Box box) {
        return (View<BOX>) views.get(box.getType());
    }
}
