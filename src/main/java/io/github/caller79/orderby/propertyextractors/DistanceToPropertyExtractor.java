package io.github.caller79.orderby.propertyextractors;

import io.github.caller79.orderby.DistanceToPropertyParser;
import io.github.caller79.orderby.Point;
import io.github.caller79.orderby.propertyacceptors.DistanceToPropertyAcceptor;

/**
 * Created by Carlos Aller on 10/01/20
 */
public abstract class DistanceToPropertyExtractor<T> extends DistanceToPropertyAcceptor implements PropertyExtractor<T> {

    @Override
    public Comparable<?> getProperty(T object, String name) {
        Double objectLat = getLatitude(object);
        Double objectLon = getLongitude(object);

        if (objectLat == null || objectLon == null) {
            return Double.MAX_VALUE;
        }

        DistanceToPropertyParser parser = getParser(name);
        if (!parser.isValid()) {
            throw new IllegalArgumentException("Cannot parse distanceTo from supplied input.");
        }
        Point point = parser.getRepresentedPoint();

        return point.distanceTo(new Point(objectLat, objectLon)); // Use the fast distance implementation
    }

     // TODO: Change this to one only method getPoint(T object);

    protected abstract Double getLatitude(T object);

    protected abstract Double getLongitude(T object);
}
