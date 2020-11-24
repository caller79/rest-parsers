package io.github.caller79.propertysetters;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Carlos Aller on 9/10/19
 */
@SupportedTypes(classes = {Object.class})
public class BeanPropertySetter<T> extends ItemPropertySetter<T> {
    static {
        ConvertUtils.register(new IntegerConverter(null), Integer.class);
        ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
        ConvertUtils.register(new DateConverter(null), Date.class);
        ConvertUtils.register(new BooleanConverter(null), Boolean.class);
    }

    @Override
    public void setProperty(T item, String propertyName, Object propertyValue) throws PropertySetException {
        try {
            if (propertyValue == null) {
                PropertyUtils.setProperty(item, propertyName, null);
            } else {
                BeanUtils.setProperty(item, propertyName, propertyValue);
            }
        } catch (IllegalAccessException | InvocationTargetException | ConversionException | NoSuchMethodException e) {
            throw new PropertySetException("Error setting property", e);
        }
    }
}
