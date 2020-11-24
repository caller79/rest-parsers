package io.github.caller79.orderby.propertyextractors;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class ParserCache<P> {
    private final ParserSupplier<P> parserSupplier;
    private final Map<String, P> cache = new ConcurrentHashMap<>();

    public ParserCache(ParserSupplier<P> parserSupplier) {
        this.parserSupplier = parserSupplier;
    }

    public P getParser(String propertyName) {
        P value = cache.get(propertyName);
        if (value == null) {
            value = parserSupplier.constructNewParser(propertyName);
            cache.put(propertyName, value);
        }
        return value;
    }

    @FunctionalInterface
    public interface ParserSupplier<P> {
        P constructNewParser(String propertyName);
    }
}
