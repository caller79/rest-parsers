package io.github.caller79.orderby;

import io.github.caller79.orderby.functions.FunctionExpression;
import io.github.caller79.orderby.functions.FunctionExpressionParser;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * Created by Carlos Aller on 10/01/20
 */
@Slf4j
public class DistanceToPropertyParser {
    public static final Pattern NUMBER_PATTERN = Pattern.compile("^-?[0-9]{1,3}(\\.[0-9]+)?$");
    public static final String EXPECTED_FUNCTION_NAME = "distanceTo";

    private FunctionExpression functionExpression;
    private boolean isValidFunctionExpression;

    public DistanceToPropertyParser(String propertyName) {
        try {
            functionExpression = new FunctionExpressionParser(":").parse(propertyName);
            isValidFunctionExpression = true;
        } catch (ParseException e) {
            isValidFunctionExpression = false;
            // Ignore it
        }
    }

    public boolean isValid() {
        return isValidFunctionExpression && functionExpression != null && EXPECTED_FUNCTION_NAME.equals(functionExpression.getName()) && functionExpression.getArguments().size() == 2
            && NUMBER_PATTERN.matcher(functionExpression.getArguments().get(0)).matches() && NUMBER_PATTERN.matcher(functionExpression.getArguments().get(1)).matches();
    }

    public Point getRepresentedPoint() {
        double latitude = Double.parseDouble(functionExpression.getArguments().get(0));
        double longitude = Double.parseDouble(functionExpression.getArguments().get(1));
        return new Point(latitude, longitude);
    }
}
