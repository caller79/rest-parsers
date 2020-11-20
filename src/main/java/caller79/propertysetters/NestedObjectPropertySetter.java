package caller79.propertysetters;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Created by Carlos Aller on 10/10/19
 */
public class NestedObjectPropertySetter<T> extends BeanPropertySetter<T> {

    public void setProperty(T item, String propertyName, Object propertyValue) throws PropertySetException {
        if (propertyValue == null) {
            super.setProperty(item, propertyName, null);
        } else {
            PropertiesUpdateRequest updateRequest = transform(propertyValue);
            try {
                Object currentValue = PropertyUtils.getProperty(item, propertyName);
                if (currentValue == null) {
                    PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(item, propertyName);
                    try {
                        currentValue = propertyDescriptor.getPropertyType().newInstance();
                    } catch (InstantiationException e) {
                        throw new PropertySetException("Cannot create new instance of " + propertyDescriptor.getPropertyType(), e);
                    }
                    BeanUtils.setProperty(item, propertyName, currentValue);
                }
                PropertySettingHelper<Object> helper = new PropertySettingHelper<>(getItemPropertySetterInitializer());
                helper.applyProperties(currentValue, updateRequest.getProperties());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new PropertySetException("Error setting property", e);
            }
        }
    }

    public void validate(String propertyName, Object propertyValue) throws PropertySetException {
        if (propertyValue != null) {
            if (!(propertyValue instanceof Map)) {
                throw new PropertySetException("Expecting a map for property " + propertyName);
            }
            Map propertyValueAsMap = (Map) propertyValue;
            ValidationUtils.expectMapKey(propertyValueAsMap, "properties", List.class, propertyName);
            Object propertiesList = propertyValueAsMap.get("properties");
            ValidationUtils.checkObjectIsAList("properties", propertiesList, item -> {
                if (!(item instanceof Map)) {
                    throw new PropertySetException("Expecting a map inside the list of properties for property " + propertyName);
                }
                Map itemAsMap = (Map) item;
                ValidationUtils.expectMapKey(itemAsMap, "name", String.class, propertyName + ".properties");
                ValidationUtils.expectMapKey(itemAsMap, "value", Object.class, propertyName + ".properties", true);
            });
        }
    }

    private PropertiesUpdateRequest transform(Object propertyValue) {
        return PropertiesUpdateRequest.fromMap((Map<String, List<Map<String, Object>>>) propertyValue);
    }
}
