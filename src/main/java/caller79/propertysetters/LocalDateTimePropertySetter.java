package caller79.propertysetters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Created by Carlos Aller on 9/10/19
 */
@SupportedTypes(classes = {LocalDateTime.class})
public class LocalDateTimePropertySetter<T> extends AbstractTimePropertySetter<T> {

    @Override
    protected Object getPropertyValueToSet(String pattern, String value) throws PropertySetException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        try {
            return LocalDateTime.parse(value, dateTimeFormatter);
        } catch (DateTimeParseException dtpe) {
            throw new PropertySetException("Error parsing date ", dtpe);
        }
    }
}
