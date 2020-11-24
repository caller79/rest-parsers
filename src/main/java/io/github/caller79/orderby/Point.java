package io.github.caller79.orderby;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

/**
 * Created by Carlos Aller on 10/01/20
 */
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Point {

    private static final int EARTH_RADIUS = 6371;

    private double lat;
    private double lng;

    /**
     * Return the distance between this point and another (in Km)
     */
    public double exactDistanceTo(Point point) {
        return acos(
            sin(toRadians(lat)) * sin(toRadians(point.getLat())) + cos(toRadians(lat)) * cos(
                toRadians(point.getLat())) * cos(toRadians(lng) - toRadians(point.getLng()))) * EARTH_RADIUS;
    }

    /**
     * Return the distance between this point and another (in Km)
     * <p>
     * Formula x = Δλ ⋅ cos φm
     * y = Δφ
     * d = R ⋅ √x² + y²
     * <p>
     * where φ is latitude, λ is longitude, R is earth’s radius (mean radius = 6,371km);
     * note that angles need to be in radians to pass to trig functions!
     *
     * @param other
     * @return
     */
    public double approxDistanceTo(Point other) {
        return Math.sqrt(distanceTo(other)) * EARTH_RADIUS;
    }

    /**
     * Return the distance between this point and another. It's not canonical distance to be displayed, just valid for
     * comparisons.
     */
    public double distanceTo(Point other) {
        double factor = cos(toRadians(lat + other.lat) / 2);
        double x = toRadians(lng - other.lng) * factor;
        double y = toRadians(lat - other.lat);
        return x * x + y * y;
    }
}
