package caller79.numericrange;

import java.util.ArrayList;
import java.util.List;
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
        return ranges != null && ranges.stream().anyMatch(numericRange -> numericRange.overlaps(range));
    }

    MultipleNumericRange join(MultipleNumericRange... others) {
        List<NumericRange> resultingRanges = new ArrayList<>(ranges);
        for (MultipleNumericRange other : others) {
            resultingRanges.addAll(other.ranges);
        }
        return MultipleNumericRange.builder().ranges(resultingRanges).build();
    }

    @Override
    public String toString() {
        if (ranges == null || ranges.isEmpty()) {
            return "[,]";
        } else {
            return ranges.stream().map(Object::toString).collect(Collectors.joining());
        }
    }
}
