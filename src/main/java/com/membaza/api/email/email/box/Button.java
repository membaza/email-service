package com.membaza.api.email.email.box;

import com.membaza.api.email.email.Box;
import com.membaza.api.email.email.BoxType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
@Data @AllArgsConstructor
public final class Button implements Box {

    private String value;
    private String href;

    @Override
    public BoxType getType() {
        return BoxType.BUTTON;
    }
}
