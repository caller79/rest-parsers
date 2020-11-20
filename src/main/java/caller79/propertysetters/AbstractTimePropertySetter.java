package caller79.propertysetters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Created by Carlos Aller on 9/10/19
 */
public abstract class AbstractTimePropertySetter<T> extends BeanPropertySetter<T> {
    public static final String FULL_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String SHORT_FORMAT = "yyyy-MM-dd";
    private static final String[] ADMITTED_FORMATS = {FULL_FORMAT, MINUTE_FORMAT, SHORT_FORMAT};

    private static final Map<String, Function<String, String>> VALUE_AUGMENTERS = new ConcurrentHashMap<>();

    static {
        VALUE_AUGMENTERS.put(FULL_FORMAT, s -> s);
        VALUE_AUGMENTERS.put(MINUTE_FORMAT, s -> s + ":00");
        VALUE_AUGMENTERS.put(SHORT_FORMAT, s -> s + " 00:00:00");
    }

    protected boolean isValidFormat(String format, String value, Locale locale) {
        LocalDateTime ldt = null;
        DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(format, locale);
        try {
            ldt = LocalDateTime.parse(value, fomatter);
            String result = ldt.format(fomatter);
            return result.equals(value);
        } catch (DateTimeParseException e) {
            try {
                LocalDate ld = LocalDate.parse(value, fomatter);
                String result = ld.format(fomatter);
                return result.equals(value);
            } catch (DateTimeParseException exp) {
                try {
                    LocalTime lt = LocalTime.parse(value, fomatter);
                    String result = lt.format(fomatter);
                    return result.equals(value);
                } catch (DateTimeParseException e2) {
                    return false;
                }
            }
        }
    }

    protected String getMatchedFormat(Object propertyValue) {
        String propertyText = String.valueOf(propertyValue);
        return Arrays.stream(ADMITTED_FORMATS).filter(admittedFormat -> isValidFormat(admittedFormat, propertyText, Locale.ENGLISH)).findFirst().orElse(null);
    }

    @Override
    public void validate(String propertyName, Object propertyValue) throws PropertySetException {
        if (propertyValue != null && getMatchedFormat(propertyValue) == null) {
            throw new PropertySetException("Expecting a Date for property " + propertyName + " with any of these formats: " + Arrays.toString(ADMITTED_FORMATS));
        }
    }

    @Override
    public void setProperty(T item, String propertyName, Object propertyValue) throws PropertySetException {
        if (propertyValue == null) {
            super.setProperty(item, propertyName, null);
        } else {
            String pattern = getMatchedFormat(propertyValue);
            if (pattern == null) {
                throw new PropertySetException("Missing validation on property " + propertyName + "?");
            } else {
                String fullDateValue = getFullFormat(pattern, String.valueOf(propertyValue));
                super.setProperty(item, propertyName, getPropertyValueToSet(FULL_FORMAT, fullDateValue));
            }
        }
    }

    // Given a passed format, adds hour, minute or seconds until it matches the full format.
    private String getFullFormat(String pattern, String dateValue) {
        return Optional.ofNullable(VALUE_AUGMENTERS.get(pattern)).map(aug -> aug.apply(dateValue)).orElse(dateValue);
    }

    protected abstract Object getPropertyValueToSet(String pattern, String value) throws PropertySetException;
}
