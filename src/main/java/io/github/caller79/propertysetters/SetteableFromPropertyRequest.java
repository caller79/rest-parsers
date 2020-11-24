package io.github.caller79.propertysetters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Carlos Aller on 9/10/19
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SetteableFromPropertyRequest {
    String name() default "";

    Class<? extends ItemPropertySetter> setter() default ItemPropertySetter.class;

    boolean ignore() default false;

    int priority() default 0;
}
