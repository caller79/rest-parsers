package io.github.caller79.orderby.propertyextractors;

import io.github.caller79.orderby.propertyacceptors.PropertyAcceptor;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Carlos Aller on 10/01/20
 */
public class CombinedPropertyExtractor<T> implements PropertyExtractor<T> {

    private final PropertyExtractor<T>[] propertyExtractors;

    @SafeVarargs
    public CombinedPropertyExtractor(PropertyExtractor<T>... propertyExtractors) {
        this.propertyExtractors = propertyExtractors.clone();
    }

    @Override
    public Comparable<?> getProperty(T object, String name) {
        for (PropertyExtractor<T> propertyExtractor : propertyExtractors) {
            if (propertyExtractor.isPropertyAccepted(name)) {
                return propertyExtractor.getProperty(object, name);
            }
        }
        throw new IllegalArgumentException("Property not found.");
    }

    @Override
    public boolean isPropertyAccepted(String name) {
        return Arrays.stream(propertyExtractors).anyMatch(acceptor -> acceptor.isPropertyAccepted(name));
    }

    @Override
    public String describe() {
        return Arrays.stream(propertyExtractors).map(PropertyAcceptor::describe).collect(Collectors.joining(", "));
    }
}
