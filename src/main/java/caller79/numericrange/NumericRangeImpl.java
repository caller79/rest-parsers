package caller79.numericrange;

@lombok.Builder
@lombok.Data
@lombok.AllArgsConstructor
class NumericRangeImpl implements NumericRange {
    private final Number start;
    private final Number end;
    private final boolean startIncluded;
    private final boolean endIncluded;

    @Override
    public Number start() {
        return start;
    }

    @Override
    public Number end() {
        return end;
    }

    @Override
    public boolean startIncluded() {
        return startIncluded;
    }

    @Override
    public boolean endIncluded() {
        return endIncluded;
    }

    @Override
    public String toString() {
        if (start != null && start.equals(end) && startIncluded && endIncluded) {
            return "[" + simplifiedNumericText(start) + "]";
        }
        return "" + (startIncluded ? '[' : '(')
            + simplifiedNumericText(start)
            + ','
            + simplifiedNumericText(end)
            + (endIncluded ? ']' : ')');
    }

    private String simplifiedNumericText(Number n) {
        if (n == null) {
            return "";
        }
        return String.valueOf(n);
    }
}
