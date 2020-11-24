package io.github.caller79.orderby.propertyextractors;

import io.github.caller79.orderby.propertyacceptors.PropertyAcceptor;

/**
 * Created by Carlos Aller on 10/01/20
 */
public interface PropertyExtractor<T> extends PropertyAcceptor {
    Comparable<?> getProperty(T object, String name);
}
