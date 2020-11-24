package io.github.caller79.propertysetters;

/**
 * Created by Carlos Aller on 11/10/19
 */
public class ListOfStringsPropertySetter<T> extends BeanPropertySetter<T> {

    @Override
    public void validate(String propertyName, Object propertyValue) throws PropertySetException {
        ValidationUtils.checkObjectIsAList(propertyName, propertyValue, item -> {
            if (!(item instanceof String)) {
                throw new PropertySetException("Expecting String's in the list " + propertyName);
            }
        });
    }
}
