package com.membaza.api.email.email.model;

import com.membaza.api.email.email.Model;
import com.membaza.api.email.email.ModelType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
@Data @AllArgsConstructor
public final class Text implements Model {

    private String value;

    @Override
    public ModelType getType() {
        return ModelType.TEXT;
    }
}
