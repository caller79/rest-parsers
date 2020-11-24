package io.github.caller79.orderby.propertyacceptors;

import io.github.caller79.orderby.DistanceToPropertyParser;
import io.github.caller79.orderby.propertyextractors.ParserCache;

/**
 * Created by Carlos Aller on 10/01/20
 */
public class DistanceToPropertyAcceptor implements PropertyAcceptor {

    private final ParserCache<DistanceToPropertyParser> cache = new ParserCache<>(this::createNewParser);

    protected DistanceToPropertyParser createNewParser(String name) {
        return new DistanceToPropertyParser(name);
    }

    protected DistanceToPropertyParser getParser(String name) {
        return cache.getParser(name);
    }

    @Override
    public boolean isPropertyAccepted(String name) {
        DistanceToPropertyParser parser = getParser(name);
        return parser.isValid();
    }

    @Override
    public String describe() {
        return "distanceTo(lat:lng)";
    }
}
