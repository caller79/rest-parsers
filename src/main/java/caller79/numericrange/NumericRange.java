package caller79.numericrange;

public interface NumericRange {
    Number start();

    Number end();

    boolean startIncluded();

    boolean endIncluded();

    default boolean contains(Number val) {
        if (start() != null && end() != null) {
            return NumberComparingUtils.isSmallerThan(start(), val, startIncluded())
                && NumberComparingUtils.isGreaterThan(end(), val, endIncluded());
        }
        if (start() != null && end() == null) {
            return NumberComparingUtils.isSmallerThan(start(), val, startIncluded());
        }
        if (start() == null && end() != null) {
            return NumberComparingUtils.isGreaterThan(end(), val, endIncluded());
        }
        return true;
    }

    default boolean overlaps(NumericRange other) {
        // Case #1. My range is open ended on both sides
        if (this.start() == null && this.end() == null) {
            return true;
        }
        // Case #2. My range is open ended on start only
        else if (this.start() == null && this.end() != null) {
            return other.start() == null || NumberComparingUtils.isGreaterThan(this.end(), other.start(), this.endIncluded() && other.startIncluded());
        }
        // Case #3. My range is open ended on end only
        else if (this.start() != null && this.end() == null) {
            return other.end() == null || NumberComparingUtils.isSmallerThan(this.start(), other.end(), this.startIncluded() && other.endIncluded());
        }
        // Case #4. My range is not open ended
        else {
            return (other.start() == null || NumberComparingUtils.isSmallerThan(other.start(), this.end(), other.startIncluded() && this.endIncluded()))
                && (other.end() == null || NumberComparingUtils.isGreaterThan(other.end(), this.start(), other.endIncluded() && this.startIncluded()));
        }
    }
}
