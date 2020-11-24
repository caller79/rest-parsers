package io.github.caller79.propertysetters;

/**
 * Created by Carlos Aller on 10/10/19
 */
@FunctionalInterface
public interface ItemPropertySetterInitializer {
    void initialize(ItemPropertySetter<?> setter);
}
