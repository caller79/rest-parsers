package caller79.propertysetters;

/**
 * Created by Carlos Aller on 9/10/19
 */
@SupportedTypes(classes = {boolean.class})
public class PrimitiveBooleanPropertySetter<T> extends BeanPropertySetter<T> {

    @Override
    public void setProperty(T item, String propertyName, Object propertyValue) throws PropertySetException {
        if (propertyValue instanceof Boolean) {
            super.setProperty(item, propertyName, propertyValue);
        } else {
            super.setProperty(item, propertyName, Boolean.valueOf(String.valueOf(propertyValue)));
        }
    }

    @Override
    public void validate(String propertyName, Object propertyValue) throws PropertySetException {
        if (propertyValue == null || !("true".equalsIgnoreCase(String.valueOf(propertyValue)) || "false".equalsIgnoreCase(String.valueOf(propertyValue)))) {
            throw new PropertySetException("Expecting a boolean for property " + propertyName);
        }
    }
}
