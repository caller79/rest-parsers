package io.github.caller79.orderby;

import java.util.List;

/**
 * Created by Carlos Aller on 21/08/19
 */
@lombok.Data
@lombok.Builder
public class OrderByClause {
    List<OrderByProperty> properties;
}
