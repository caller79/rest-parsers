package caller79.numericrange;

final class NumberComparingUtils {

    private NumberComparingUtils() {
        // Prevents instantiation
    }

    public static boolean isGreaterThan(Number val, Number val2, boolean includeEquals) {
        return val.doubleValue() > val2.doubleValue() || (includeEquals && val.doubleValue() == val2.doubleValue());
    }

    public static boolean isSmallerThan(Number val, Number val2, boolean includeEquals) {
        return val.doubleValue() < val2.doubleValue() || (includeEquals && val.doubleValue() == val2.doubleValue());
    }
}
