package io.github.caller79.propertysetters;

/**
 * Created by Carlos Aller on 9/10/19
 */
@SupportedTypes(classes = {Integer.class})
public class IntegerPropertySetter<T> extends BeanPropertySetter<T> {

    @Override
    public void setProperty(T item, String propertyName, Object propertyValue) throws PropertySetException {
        if (propertyValue == null) {
            super.setProperty(item, propertyName, null);
        } else if (propertyValue instanceof Integer) {
            super.setProperty(item, propertyName, propertyValue);
        } else {
            super.setProperty(item, propertyName, Integer.valueOf(String.valueOf(propertyValue)));
        }
    }

    @Override
    public void validate(String propertyName, Object propertyValue) throws PropertySetException {
        if (propertyValue != null) {
            try {
                Integer.parseInt(String.valueOf(propertyValue));
            } catch (NumberFormatException nfe) {
                throw new PropertySetException("Expecting a number for property " + propertyName, nfe);
            }
        }
    }
}
