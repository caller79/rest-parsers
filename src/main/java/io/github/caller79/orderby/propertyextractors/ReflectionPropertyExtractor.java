package io.github.caller79.orderby.propertyextractors;

import io.github.caller79.orderby.propertyacceptors.ListOfPropertyNamesPropertyAcceptor;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Created by Carlos Aller on 10/01/20
 */
@lombok.extern.slf4j.Slf4j
public class ReflectionPropertyExtractor<T> extends ListOfPropertyNamesPropertyAcceptor implements PropertyExtractor<T> {
    public ReflectionPropertyExtractor(String... allowedProperties) {
        super(allowedProperties);
    }

    public ReflectionPropertyExtractor(Class<? extends T> clazz) {
        super(getClassProperties(clazz));
    }

    public ReflectionPropertyExtractor(Class<? extends T> clazz, String... excludedProperties) {
        super(getClassProperties(clazz, excludedProperties));
    }

    private static <T> String[] getClassProperties(Class<T> clazz, String... excludedProperties) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
        return Arrays.stream(propertyDescriptors)
            .filter(propertyDescriptor -> {
                Class<?> propertyClass = propertyDescriptor.getPropertyType();
                return ClassUtils.isPrimitiveOrWrapper(propertyClass)
                    || propertyClass.isEnum()
                    || Comparable.class.isAssignableFrom(propertyClass);
            })
            .map(FeatureDescriptor::getName)
            .filter(s -> !ArrayUtils.contains(excludedProperties, s))
            .toArray(String[]::new);
    }

    @Override
    public Comparable<?> getProperty(T object, String name) {
        try {
            Object property = PropertyUtils.getProperty(object, name);
            if (property instanceof Comparable) {
                return (Comparable<?>) property;
            } else if (property == null) {
                return null;
            } else {
                return property.toString();
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Error: ", e);
        }
        return null;
    }
}
