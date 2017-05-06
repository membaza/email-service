package com.membaza.api.email.email;

import com.membaza.api.email.email.model.Button;
import com.membaza.api.email.email.model.Text;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * Converts a MongoDB object into a {@link Model}. The exact implementation will
 * depend on the {@link Model#getType() type} field.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
@ReadingConverter
public final class ModelConverter implements Converter<DBObject, Model> {

    @Override
    public Model convert(DBObject dbObject) {
        final String type = dbObject.get("type").toString();
        switch (ModelType.valueOf(type.toLowerCase())) {
            case TEXT: return new Text(
                stringOrElse(dbObject.get("value"), "")
            );
            case BUTTON: return new Button(
                stringOrElse(dbObject.get("value"), "Click Here"),
                stringOrElse(dbObject.get("href"), "")
            );
        }

        throw new UnsupportedOperationException(
            "Unsupported model type '" + type + "'."
        );
    }

    private String stringOrNull(Object obj) {
        return stringOrElse(obj, null);
    }

    private String stringOrElse(Object obj, String otherwise) {
        return obj == null ? otherwise : String.valueOf(obj);
    }
}