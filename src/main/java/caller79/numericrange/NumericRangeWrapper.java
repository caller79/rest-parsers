package caller79.numericrange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NumericRangeWrapper {

    public MultipleNumericRange wrap(Collection<? extends Number> numbers, WrapOptions options) {
        List<Number> sortedNumbers = new ArrayList<>(numbers);
        sortedNumbers.sort(Comparator.comparingDouble(Number::doubleValue));
        return wrapSorted(sortedNumbers, options.maxExpressions, false);
    }

    public MultipleNumericRange wrapDiscrete(Collection<Long> numbers, WrapOptions options) {
        List<Long> sortedNumbers = new ArrayList<>(numbers);
        sortedNumbers.sort(Long::compareTo);
        return wrapSorted(sortedNumbers, options.maxExpressions, true);
    }

    private MultipleNumericRange wrapSorted(List<? extends Number> numbers, int maxExpressions, boolean discreteNumbers) {
        if (numbers.isEmpty()) {
            return MultipleNumericRange.builder().ranges(Collections.singletonList(new NumericRangeImpl(0, 0, false, false))).build();
        }
        MultipleNumericRange allItemsRange = withAllIndividualItems(numbers, discreteNumbers);
        if (allItemsRange.getRanges()
            .stream()
            .mapToInt(numericRange -> {
                if (numericRange.start().equals(numericRange.end())) {
                    return 1;
                }
                return 2;
            })
            .sum() <= maxExpressions * 2) {
            return allItemsRange;
        }
        if (maxExpressions <= 1) {
            return MultipleNumericRange.builder().ranges(Collections.singletonList(NumericRangeImpl.builder()
                .startIncluded(true)
                .endIncluded(true)
                .start(numbers.get(0))
                .end(numbers.get(numbers.size() - 1))
                .build())).build();
        }

        int biggerGapPositionStart = findBiggerGapPositionStart(numbers);
        List<? extends Number> firstHalf = numbers.subList(0, biggerGapPositionStart + 1);
        List<? extends Number> secondHalf = numbers.subList(biggerGapPositionStart + 1, numbers.size());
        int firstMaxExpressionsAllowance = maxExpressions * firstHalf.size() / numbers.size();
        firstMaxExpressionsAllowance = firstMaxExpressionsAllowance == 0 ? 1 : firstMaxExpressionsAllowance;
        int secondMaxExpressionsAllowance = maxExpressions - firstMaxExpressionsAllowance;
        MultipleNumericRange firstRange = wrapSorted(firstHalf, firstMaxExpressionsAllowance, discreteNumbers);
        MultipleNumericRange secondRange = wrapSorted(secondHalf, secondMaxExpressionsAllowance, discreteNumbers);
        return firstRange.join(secondRange);
    }

    private int findBiggerGapPositionStart(List<? extends Number> numbers) {
        int pos = 0;
        double maxGap = 0;
        for (int i = 0; i < numbers.size() - 1; i++) {
            Number current = numbers.get(i);
            Number next = numbers.get(i + 1);
            double currentGap = next.doubleValue() - current.doubleValue();
            if (currentGap > maxGap) {
                maxGap = currentGap;
                pos = i;
            }
        }
        return pos;
    }

    private MultipleNumericRange withAllIndividualItems(List<? extends Number> numbers, boolean discreteNumbers) {
        if (discreteNumbers) {
            List<NumericRange> ranges = new ArrayList<>();
            Number current = null;
            Number next = null;
            for (Number number : numbers) {
                if (current == null) {
                    current = number;
                    next = number;
                } else {
                    if (number.longValue() == next.longValue() + 1) {
                        next = number;
                    } else {
                        ranges.add(NumericRangeImpl.builder().startIncluded(true).endIncluded(true).start(current).end(next).build());
                        current = number;
                        next = number;
                    }
                }
            }
            ranges.add(NumericRangeImpl.builder().startIncluded(true).endIncluded(true).start(current).end(next).build());
            return MultipleNumericRange.builder().ranges(ranges).build();
        } else {
            return MultipleNumericRange.builder()
                .ranges(numbers.stream().map((Function<Number, NumericRange>) number -> NumericRangeImpl.builder().startIncluded(true).endIncluded(true).start(number).end(number).build()).collect(Collectors.toList()))
                .build();
        }
    }

    @lombok.Builder
    @lombok.Data
    public static class WrapOptions {
        @lombok.Builder.Default
        private int maxExpressions = 50;
    }
}
