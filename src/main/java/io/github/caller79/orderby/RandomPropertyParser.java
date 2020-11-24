package io.github.caller79.orderby;

import io.github.caller79.orderby.functions.FunctionExpression;
import io.github.caller79.orderby.functions.FunctionExpressionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;

/**
 * Created by Carlos Aller on 10/01/20
 */
@Slf4j
public class RandomPropertyParser {

    public static final String EXPECTED_FUNCTION_NAME = "random";
    private FunctionExpression functionExpression;
    private boolean isValidFunctionExpression;

    public RandomPropertyParser(String propertyName) {
        try {
            functionExpression = new FunctionExpressionParser().parse(propertyName);
            isValidFunctionExpression = true;
        } catch (ParseException e) {
            isValidFunctionExpression = false;
            // Ignore it
        }
    }

    public boolean isValid() {
        return isValidFunctionExpression && functionExpression != null && EXPECTED_FUNCTION_NAME.equals(functionExpression.getName()) && functionExpression.getArguments().size() == 1;
    }

    public String getSeed() {
        return StringUtils.defaultString(functionExpression.getArguments().get(0));
    }
}
