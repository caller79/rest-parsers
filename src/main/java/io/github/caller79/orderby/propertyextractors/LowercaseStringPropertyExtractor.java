package io.github.caller79.orderby.propertyextractors;

import java.util.Locale;

public class LowercaseStringPropertyExtractor<T> extends ReflectionPropertyExtractor<T> {

    public LowercaseStringPropertyExtractor(String... allowedProperties) {
        super(allowedProperties);
    }

    @Override
    public Comparable<?> getProperty(T object, String name) {
        Comparable<?> property = super.getProperty(object, name);
        if (property instanceof String) {
            return ((String) property).toLowerCase(Locale.ENGLISH).trim();
        }
        return property;
    }


}
