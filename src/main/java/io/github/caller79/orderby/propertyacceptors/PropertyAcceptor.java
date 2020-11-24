package io.github.caller79.orderby.propertyacceptors;

/**
 * Created by Carlos Aller on 10/01/20
 */
@FunctionalInterface
public interface PropertyAcceptor {
    boolean isPropertyAccepted(String name);

    default String describe() {
        return "";
    }
}
