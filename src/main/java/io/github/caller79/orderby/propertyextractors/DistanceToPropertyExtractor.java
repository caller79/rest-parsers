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
        Point objectPoint = getCoordinates(object);
        if (objectPoint == null) {
            return Double.MAX_VALUE;
        }
        double objectLat = objectPoint.getLat();
        double objectLon = objectPoint.getLng();

        DistanceToPropertyParser parser = getParser(name);
        if (!parser.isValid()) {
            throw new IllegalArgumentException("Cannot parse distanceTo from supplied input.");
        }
        Point point = parser.getRepresentedPoint();

        return point.distanceTo(new Point(objectLat, objectLon)); // Use the fast distance implementation
    }

    protected abstract Point getCoordinates(T object);
}
