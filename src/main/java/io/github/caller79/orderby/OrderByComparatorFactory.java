package io.github.caller79.orderby;

import io.github.caller79.orderby.propertyextractors.PropertyExtractor;

import java.text.ParseException;

/**
 * Created by Carlos Aller on 10/01/20
 */
public final class OrderByComparatorFactory {

    private OrderByComparatorFactory() {
        // Prevents instantiation
    }

    public static <T> OrderByComparator<T> getComparator(String orderByExpression, PropertyExtractor<T> propertyExtractor) throws ParseException {
        OrderByClause parseResult = OrderByClauseParser.parse(orderByExpression, propertyExtractor);
        return new OrderByComparator<>(parseResult, propertyExtractor);
    }
}
