package io.github.caller79.orderby.functions;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class FunctionExpressionParser {
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("^([a-zA-Z]+)\\(([^()]*)\\)$");

    @lombok.Builder.Default
    private String argumentsSeparator = ",";

    public FunctionExpression parse(String s) throws ParseException {
        Matcher matcher = FUNCTION_PATTERN.matcher(s);
        if (matcher.matches()) {
            String functionName = matcher.group(1);
            String arguments = matcher.group(2);
            List<String> argumentsList;
            if (StringUtils.isEmpty(arguments)) {
                argumentsList = Collections.emptyList();
            } else {
                argumentsList = Arrays.asList(arguments.split(argumentsSeparator));
            }
            return new FunctionExpression(functionName, Collections.unmodifiableList(argumentsList));
        } else {
            throw new ParseException("Unparseable expression.", 0);
        }
    }
}
