package io.github.caller79.propertysetters;

/**
 * Created by Carlos Aller on 11/10/19
 */
public abstract class NestedObjectByIdPropertySetter<T, I> extends BeanPropertySetter<T> {

    @Override
    public void setProperty(T item, String propertyName, Object propertyValue) throws PropertySetException {
        if (propertyValue == null) {
            super.setProperty(item, propertyName, null);
        } else {
            I nestedObject = getById(((Integer) propertyValue).longValue());
            if (nestedObject == null) {
                throw new PropertySetException("Not found id " + propertyValue + " for property " + propertyName);
            }
            retrofit(nestedObject, item, propertyName);
            super.setProperty(item, propertyName, nestedObject);
        }
    }

    @Override
    public void validate(String propertyName, Object propertyValue) throws PropertySetException {
        if (propertyValue != null && !(propertyValue instanceof Integer)) {
            throw new PropertySetException("Expecting an id for property " + propertyName);
        }
    }

    protected abstract void retrofit(I nestedObject, T item, String propertyName);

    protected abstract I getById(Long id);
}
