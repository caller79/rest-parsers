package caller79.numericrange;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumericRangeFactory {
    public static final String DIGIT_REGEXP = "-?[0-9]+(\\.[0-9]+)?";
    public static final String NUMERIC_RANGE_REGEXP = "(?<range>[\\[\\(](" + DIGIT_REGEXP + ")?(,(" + DIGIT_REGEXP + ")?)?[\\)\\]])";
    public static final Pattern NUMERIC_RANGE_PATTERN = Pattern.compile(NUMERIC_RANGE_REGEXP);
    public static final String MULTIPLE_NUMERIC_RANGE_REGEXP = "^(?<ranges>" + NUMERIC_RANGE_REGEXP + ")+$";
    public static final Pattern MULTIPLE_NUMERIC_RANGE_PATTERN = Pattern.compile(MULTIPLE_NUMERIC_RANGE_REGEXP);

    public MultipleNumericRange parse(String textRepresentation) {
        Matcher matcher = MULTIPLE_NUMERIC_RANGE_PATTERN.matcher(textRepresentation);
        if (matcher.matches()) {
            // String[] ranges = textRepresentation.split("((?<=\\))|(?=\\))|(?<=\\])|(?=\\]))");
            String[] ranges = textRepresentation.split("((?<=\\))|(?<=\\]))");
            List<NumericRange> numericRanges = new ArrayList<>();
            for (String range : ranges) {
                numericRanges.add(getRangeFrom(range));
            }
            return MultipleNumericRange.builder().ranges(Collections.unmodifiableList(numericRanges)).build();
        } else {
            throw new IllegalArgumentException("Invalid numeric range representation.");
        }
    }

    private NumericRange getRangeFrom(String range) {
        Matcher rangeMatcher = NUMERIC_RANGE_PATTERN.matcher(range);
        Double start = null;
        Double end = null;
        boolean startIncluded;
        boolean endIncluded;
        if (rangeMatcher.matches()) {
            startIncluded = isStartIncluded(range);
            endIncluded = isEndIncluded(range);
            String[] numbers = getNumbersFromSimpleRange(range);
            if (StringUtils.isNotEmpty(numbers[0])) {
                start = Double.parseDouble(numbers[0]);
            }
            if (StringUtils.isNotEmpty(numbers[1])) {
                end = Double.parseDouble(numbers[1]);
            }
            return NumericRangeImpl.builder()
                .startIncluded(startIncluded)
                .endIncluded(endIncluded)
                .start(start)
                .end(end)
                .build();
        } else {
            throw new IllegalArgumentException("Invalid range");
        }
    }

    private String[] getNumbersFromSimpleRange(String range) {
        boolean endsWithComma = range.endsWith(",]") || range.endsWith(",)");
        String[] numbers = range.substring(1, range.length() - 1).split(",");
        if (numbers.length == 0) {
            numbers = new String[]{"", ""};
        }
        if (numbers.length == 1) {
            numbers = new String[]{numbers[0], endsWithComma ? "" : numbers[0]}; //Exact number match
        }
        if (numbers.length != 2) {
            throw new IllegalArgumentException("Internal parse error. Invalid numbers in range.");
        }
        return numbers;
    }

    private boolean isEndIncluded(String range) {
        boolean endIncluded;
        if (range.charAt(range.length() - 1) == ')') {
            endIncluded = false;
        } else if (range.charAt(range.length() - 1) == ']') {
            endIncluded = true;
        } else {
            throw new IllegalArgumentException("Internal parse error. Unexpected end character " + range.charAt(range.length() - 1));
        }
        return endIncluded;
    }

    private boolean isStartIncluded(String range) {
        boolean startIncluded;
        if (range.charAt(0) == '(') {
            startIncluded = false;
        } else if (range.charAt(0) == '[') {
            startIncluded = true;
        } else {
            throw new IllegalArgumentException("Internal parse error. Unexpected start character " + range.charAt(0));
        }
        return startIncluded;
    }
}
