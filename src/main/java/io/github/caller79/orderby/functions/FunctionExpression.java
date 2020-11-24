package io.github.caller79.orderby.functions;

import java.util.List;

@lombok.Data
@lombok.AllArgsConstructor
public class FunctionExpression {
    private final String name;
    private final List<String> arguments;
}
