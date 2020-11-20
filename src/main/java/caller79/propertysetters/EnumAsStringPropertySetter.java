package caller79.propertysetters;

import java.lang.reflect.Field;

/**
 * Created by Carlos Aller on 9/12/19
 */
@SupportedTypes(classes = {Enum.class})
public class EnumAsStringPropertySetter<T> extends BeanPropertySetter<T> {

    @Override
    public void setProperty(T item, String propertyName, Object propertyValue) throws PropertySetException {
        if (propertyValue == null) {
            super.setProperty(item, propertyName, null);
        } else {
            try {
                setPropertyFromString(item, propertyName, String.valueOf(propertyValue));
            } catch (NoSuchFieldException e) {
                throw new PropertySetException("Can't set property " + propertyName + " as an enum.", e);
            }
        }
    }

    private void setPropertyFromString(T item, String propertyName, String value) throws NoSuchFieldException, PropertySetException {
        Field field = item.getClass().getDeclaredField(propertyName);
        Class<?> fieldClass = field.getType();
        if (Enum.class.isAssignableFrom(fieldClass)) {
            Enum[] possibleEnumValues = (Enum[]) fieldClass.getEnumConstants();
            for (Enum possibleEnumValue : possibleEnumValues) {
                if (possibleEnumValue.toString().equals(value)) {
                    super.setProperty(item, propertyName, possibleEnumValue);
                    return;
                }
            }
        }
        throw new PropertySetException("Invalid value for enum.");
    }
}
