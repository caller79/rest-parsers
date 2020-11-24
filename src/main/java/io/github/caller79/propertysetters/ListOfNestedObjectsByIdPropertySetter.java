package io.github.caller79.propertysetters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Carlos Aller on 11/10/19
 */
public abstract class ListOfNestedObjectsByIdPropertySetter<T, I> extends BeanPropertySetter<T> {

    @Override
    public void setProperty(T item, String propertyName, Object propertyValue) throws PropertySetException {
        if (propertyValue == null) {
            setListValue(item, propertyName, Collections.emptyList());
        } else {
            List<I> existingTypes = getListValue(item, propertyName);
            List<I> newTypes = new ArrayList<>();
            List<Long> value = ((List<Integer>) propertyValue).stream().map(Long::valueOf).collect(Collectors.toList());

            if (existingTypes != null) {
                for (I next : existingTypes) {
                    if (value.contains(getId(next))) {
                        value.remove(getId(next));
                        newTypes.add(next);
                    }
                }
            }

            // If there are more items coming from the API than the item already has, the extra ones need to be added.
            for (Long id : value) {
                I type = getById(id);
                if (type == null) {
                    throw new PropertySetException("No item with id #" + id + " setting " + propertyName);
                }
                newTypes.add(type);
            }
            if (getListValue(item, propertyName) == null) {
                setListValue(item, propertyName, new ArrayList<>());
            }
            getListValue(item, propertyName).clear();
            getListValue(item, propertyName).addAll(newTypes);
        }
    }

    @Override
    public void validate(String propertyName, Object propertyValue) throws PropertySetException {
        ValidationUtils.checkObjectIsAListOfIds(propertyName, propertyValue);
    }

    protected abstract void setListValue(T item, String propertyName, List<I> list);

    protected abstract List<I> getListValue(T item, String propertyName);

    protected abstract Long getId(I nestedItem);

    protected abstract I getById(Long id);
}
