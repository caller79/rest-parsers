package io.github.caller79.orderby.propertyacceptors;

import io.github.caller79.orderby.RandomPropertyParser;
import io.github.caller79.orderby.propertyextractors.ParserCache;

/**
 * Created by Carlos Aller on 10/01/20
 */
public class RandomPropertyAcceptor implements PropertyAcceptor {
    private final ParserCache<RandomPropertyParser> cache = new ParserCache<>(RandomPropertyParser::new);

    protected RandomPropertyParser getParser(String name) {
        return cache.getParser(name);
    }

    @Override
    public boolean isPropertyAccepted(String name) {
        RandomPropertyParser parser = getParser(name);
        return parser.isValid();
    }

    @Override
    public String describe() {
        return "random(seed)";
    }
}
