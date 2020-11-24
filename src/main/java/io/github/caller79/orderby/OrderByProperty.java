package io.github.caller79.orderby;

/**
 * Created by Carlos Aller on 21/08/19
 */
@lombok.Data
@lombok.Builder
public class OrderByProperty {
    private String name;
    private boolean descending;
}
