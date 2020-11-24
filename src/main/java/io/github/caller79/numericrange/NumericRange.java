package io.github.caller79.numericrange;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;


@lombok.Builder
@lombok.Data
@lombok.AllArgsConstructor
public class NumericRange {
    private final Number start;
    private final Number end;
    private final boolean startIncluded;
    private final boolean endIncluded;

    public Number start() {
        return start;
    }

    public Number end() {
        return end;
    }

    public boolean startIncluded() {
        return startIncluded;
    }

    public boolean endIncluded() {
        return endIncluded;
    }

    public boolean contains(Number val) {
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

    public boolean overlaps(NumericRange other) {
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

    public String toString(ToStringCustomizer toStringCustomizer) {
        StringBuilder sb = new StringBuilder();
        if (start() == null && end() == null) {
            return toStringCustomizer.getAlwaysTrueExpression();
        } else {
            if (isAnExactNumber()) {
                sb.append(toStringCustomizer.getPropertyName());
                sb.append(toStringCustomizer.getEq().apply(toStringCustomizer.getNumberToString().apply(start())));
            } else {
                if (start() == null) {
                    sb.append(toStringCustomizer.getNullStart().apply(startIncluded()));
                } else {
                    sb.append(toStringCustomizer.getPropertyName());
                    sb.append(startIncluded() ? toStringCustomizer.getGe().apply(toStringCustomizer.getNumberToString().apply(start())) : toStringCustomizer.getGt().apply(toStringCustomizer.getNumberToString().apply(start())));
                }
                sb.append(getAdequateJoiner(toStringCustomizer));
                if (end() == null) {
                    sb.append(toStringCustomizer.getNullEnd().apply(endIncluded()));
                } else {
                    sb.append(toStringCustomizer.getPropertyName());
                    sb.append(endIncluded() ? toStringCustomizer.getLe().apply(toStringCustomizer.getNumberToString().apply(end())) : toStringCustomizer.getLt().apply(toStringCustomizer.getNumberToString().apply(end())));
                }
            }
        }
        return sb.toString();
    }

    private boolean isAnExactNumber() {
        return startIncluded() && endIncluded() && start() != null && end() != null && start().equals(end());
    }

    @Override
    public String toString() {
        return toString(ToStringCustomizer.numericRangeCustomizer());
    }

    private String getAdequateJoiner(ToStringCustomizer toStringCustomizer) {
        return toStringCustomizer.isApplyJoinerIfAnyEmpty() || (start() != null && end() != null) ? toStringCustomizer.getJoiner() : "";
    }

    @lombok.Builder
    @lombok.Data
    public static class ToStringCustomizer {
        @lombok.Builder.Default
        String propertyName = "x";
        @lombok.Builder.Default
        String alwaysTrueExpression = "true";
        @lombok.Builder.Default
        Function<Boolean, String> nullStart = b -> "";
        @lombok.Builder.Default
        Function<Boolean, String> nullEnd = b -> "";
        @lombok.Builder.Default
        Function<String, String> eq = s -> StringUtils.isEmpty(s) ? "" : ("=" + s);
        @lombok.Builder.Default
        Function<String, String> gt = s -> StringUtils.isEmpty(s) ? "" : (">" + s);
        @lombok.Builder.Default
        Function<String, String> lt = s -> StringUtils.isEmpty(s) ? "" : ("<" + s);
        @lombok.Builder.Default
        Function<String, String> ge = s -> StringUtils.isEmpty(s) ? "" : (">=" + s);
        @lombok.Builder.Default
        Function<String, String> le = s -> StringUtils.isEmpty(s) ? "" : ("<=" + s);
        @lombok.Builder.Default
        String joiner = " && ";
        @lombok.Builder.Default
        boolean applyJoinerIfAnyEmpty = false;
        @lombok.Builder.Default
        Function<Number, String> numberToString = number -> {
            if (number.intValue() == number.doubleValue()) {
                return String.valueOf(number.intValue());
            }
            return String.valueOf(number);
        };

        public static ToStringCustomizer sqlCustomizer(String propertyName) {
            return ToStringCustomizer.builder()
                .alwaysTrueExpression("1=1")
                .joiner(" AND ")
                .propertyName(propertyName)
                .build();
        }

        public static ToStringCustomizer javascriptCustomizer(String propertyName) {
            return ToStringCustomizer.builder()
                .propertyName(propertyName)
                .build();
        }

        public static ToStringCustomizer numericRangeCustomizer() {
            return ToStringCustomizer.builder()
                .propertyName("")
                .alwaysTrueExpression("(,)")
                .gt(s -> "(" + s)
                .lt(s -> s + ")")
                .ge(s -> "[" + s)
                .le(s -> s + "]")
                .eq(s -> "[" + s + "]")
                .nullStart(b -> b ? "[" : "(")
                .nullEnd(b -> b ? "]" : ")")
                .joiner(",")
                .applyJoinerIfAnyEmpty(true)
                .build();
        }
    }
}
