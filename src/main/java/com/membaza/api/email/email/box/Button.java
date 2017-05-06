package com.membaza.api.email.email.box;

import com.membaza.api.email.email.Model;
import com.membaza.api.email.email.ModelType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
@Data @AllArgsConstructor
public final class Button implements Model {

    private String value;
    private String href;

    @Override
    public ModelType getType() {
        return ModelType.BUTTON;
    }
}
