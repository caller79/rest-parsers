package io.github.caller79.orderby.propertyextractors;

/**
 * Created by Carlos Aller on 13/01/20
 */
public class SimplePropertyExtractorBuilder<T> {
    private String name;
    private PropertyReader<T> reader;

    public SimplePropertyExtractorBuilder<T> forProperty(String name) {
        this.name = name;
        return this;
    }

    public SimplePropertyExtractorBuilder<T> reading(PropertyReader<T> reader) {
        this.reader = reader;
        return this;
    }

    public PropertyExtractor<T> build() {
        return new PropertyExtractor<T>() {
            @Override
            public Comparable<?> getProperty(T object, String name) {
                return reader.readProperty(object);
            }

            @Override
            public boolean isPropertyAccepted(String name) {
                return SimplePropertyExtractorBuilder.this.name.equals(name);
            }

            @Override
            public String describe() {
                return name;
            }
        };

    }

    @FunctionalInterface
    public interface PropertyReader<T> {
        Comparable<?> readProperty(T object);
    }
}
