package io.github.caller79.numericrange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@lombok.Builder
@lombok.Getter
@lombok.Data
public class MultipleNumericRange {
    private final List<NumericRange> ranges;

    public boolean contains(Number value) {
        return ranges != null && ranges.stream().anyMatch(numericRange -> numericRange.contains(value));
    }

    public boolean overlaps(NumericRange range) {
        return getRanges() != null && getRanges().stream().anyMatch(numericRange -> numericRange.overlaps(range));
    }

    public boolean overlaps(MultipleNumericRange range) {
        return getRanges() != null && getRanges().stream().anyMatch(numericRange -> {
            if (range.getRanges() == null) {
                return false;
            }
            return range.getRanges().stream().anyMatch(nr -> nr.overlaps(numericRange));
        });
    }

    public MultipleNumericRange join(MultipleNumericRange... others) {
        List<NumericRange> resultingRanges = new ArrayList<>(getRanges());
        for (MultipleNumericRange other : others) {
            resultingRanges.addAll(other.getRanges());
        }
        return MultipleNumericRange.builder().ranges(Collections.unmodifiableList(resultingRanges)).build();
    }

    public String toString(ToStringCustomizer toStringCustomizer) {
        if (getRanges() == null || getRanges().isEmpty()) {
            return toStringCustomizer.getWrapper().apply(toStringCustomizer.getEmptyValue());
        }
        return getRanges().stream()
            .map(numericRange -> numericRange.toString(toStringCustomizer.getNumericRangeCustomizer()))
            .map(toStringCustomizer.getWrapper())
            .collect(toStringCustomizer.getCollector());
    }

    @Override
    public String toString() {
        return toString(ToStringCustomizer.numericRangeCustomizer());
    }

    @lombok.Builder
    @lombok.Data
    static class ToStringCustomizer {
        @lombok.Builder.Default
        NumericRange.ToStringCustomizer numericRangeCustomizer = NumericRange.ToStringCustomizer.numericRangeCustomizer();
        @lombok.Builder.Default
        String emptyValue = "(,)";
        @lombok.Builder.Default
        Function<String, String> wrapper = s -> "(" + s + ")";
        @lombok.Builder.Default
        Collector<? super String, ?, String> collector = Collectors.joining("");

        static ToStringCustomizer sqlCustomizer(String propertyName) {
            return ToStringCustomizer.builder()
                .numericRangeCustomizer(NumericRange.ToStringCustomizer.sqlCustomizer(propertyName))
                .emptyValue("1=1")
                .collector(Collectors.joining(" OR "))
                .build();
        }

        static ToStringCustomizer javascriptCustomizer(String propertyName) {
            return ToStringCustomizer.builder()
                .numericRangeCustomizer(NumericRange.ToStringCustomizer.javascriptCustomizer(propertyName))
                .emptyValue("true")
                .collector(Collectors.joining(" || "))
                .build();
        }

        static ToStringCustomizer numericRangeCustomizer() {
            return ToStringCustomizer.builder()
                .wrapper(s -> s)
                .numericRangeCustomizer(NumericRange.ToStringCustomizer.numericRangeCustomizer())
                .build();
        }
    }
}
