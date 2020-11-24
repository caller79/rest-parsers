package io.github.caller79.orderby;

import io.github.caller79.orderby.propertyextractors.PropertyExtractor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Carlos Aller on 21/08/19
 */
@lombok.RequiredArgsConstructor
public class OrderByComparator<T> implements Comparator<T> {

    private final OrderByClause orderByClause;
    private final PropertyExtractor<T> propertyExtractor;

    @Override
    public int compare(T o1, T o2) {
        List<OrderByProperty> compareProperties = orderByClause.getProperties();
        if (compareProperties != null) {
            for (OrderByProperty orderByProperty : compareProperties) {
                Comparable c1 = propertyExtractor.getProperty(o1, orderByProperty.getName());
                Comparable c2 = propertyExtractor.getProperty(o2, orderByProperty.getName());
                int comparison = ObjectUtils.compare(c1, c2);
                if (comparison != 0) {
                    return comparison * (orderByProperty.isDescending() ? -1 : 1);
                }
            }
        }
        return 0;
    }
}
