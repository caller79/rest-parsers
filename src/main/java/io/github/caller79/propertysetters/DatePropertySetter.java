package io.github.caller79.propertysetters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Carlos Aller on 9/10/19
 */
@SupportedTypes(classes = {Date.class})
public class DatePropertySetter<T> extends AbstractTimePropertySetter<T> {

    @Override
    protected Object getPropertyValueToSet(String pattern, String value) throws PropertySetException {
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return dateFormat.parse(value);
        } catch (ParseException e) {
            throw new PropertySetException("Invalid date format " + pattern + " for input " + value, e);
        }
    }
}
