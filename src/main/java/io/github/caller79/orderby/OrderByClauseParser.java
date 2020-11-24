package io.github.caller79.orderby;

import io.github.caller79.orderby.propertyacceptors.ListOfPropertyNamesPropertyAcceptor;
import io.github.caller79.orderby.propertyacceptors.PropertyAcceptor;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Carlos Aller on 21/08/19
 */
public final class OrderByClauseParser {

    public static final String DESC = "DESC";
    public static final String ASC = "ASC";

    private OrderByClauseParser() {
        // Prevents instantiation
    }

    public static OrderByClause parse(String text, PropertyAcceptor acceptor) throws ParseException {

        if (StringUtils.isEmpty(text)) {
            return OrderByClause.builder().build();
        }
        List<OrderByProperty> properties = new ArrayList<>();
        String[] clauses = text.split(",");
        for (int i = 0; i < clauses.length; i++) {
            String clause = clauses[i].trim();
            String upperClause = clause.toUpperCase(Locale.ENGLISH);
            boolean descending = false;
            String propertyName;
            if (upperClause.endsWith(DESC)) {
                propertyName = clause.substring(0, upperClause.length() - DESC.length()).trim();
                descending = true;
            } else if (upperClause.endsWith(ASC)) {
                propertyName = clause.substring(0, upperClause.length() - ASC.length()).trim();
            } else {
                propertyName = clause.trim();
            }
            if (acceptor.isPropertyAccepted(propertyName)) {
                properties.add(OrderByProperty.builder().name(propertyName).descending(descending).build());
            } else {
                throw new ParseException("Invalid clause in position " + i, i);
            }
        }
        return OrderByClause.builder().properties(properties).build();
    }

    public static OrderByClause parse(String text, String... allowedProperties) throws ParseException {
        return parse(text, new ListOfPropertyNamesPropertyAcceptor(allowedProperties));
    }

}
