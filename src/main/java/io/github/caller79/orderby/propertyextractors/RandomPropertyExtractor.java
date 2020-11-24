package io.github.caller79.orderby.propertyextractors;

import io.github.caller79.orderby.RandomPropertyParser;
import io.github.caller79.orderby.propertyacceptors.RandomPropertyAcceptor;

/**
 * Created by Carlos Aller on 10/01/20
 */
public abstract class RandomPropertyExtractor<T> extends RandomPropertyAcceptor implements PropertyExtractor<T> {
    @Override
    public Comparable<?> getProperty(T object, String name) {
        RandomPropertyParser parser = getParser(name);
        if (!parser.isValid()) {
            throw new IllegalArgumentException("Cannot parse random seed from supplied input.");
        }
        String seed = "A" + parser.getSeed();
        int multiplier = Math.abs(seed.hashCode() % 10000) + 3;
        int module = 100001;
        long id = getId(object).hashCode();
        return (id * multiplier) % module;
    }

    protected abstract String getId(T item);
}
