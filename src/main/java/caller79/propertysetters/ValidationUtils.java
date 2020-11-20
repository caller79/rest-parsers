package caller79.propertysetters;

import java.util.List;
import java.util.Map;

/**
 * Created by Carlos Aller on 9/10/19
 */
public final class ValidationUtils {
    private ValidationUtils() {
        // Prevents instantiation
    }

    public static void checkObjectIsAList(String propertyName, Object propertyValue, CustomItemValidator itemValidator) throws PropertySetException {
        if (propertyValue != null) {
            if (!(propertyValue instanceof List)) {
                throw new PropertySetException("Expecting a list for property " + propertyName);
            }
            List valueAsList = (List) propertyValue;
            for (Object element : valueAsList) {
                itemValidator.validate(element);
            }
        }
    }

    public static void checkObjectIsAListOfIds(String propertyName, Object propertyValue) throws PropertySetException {
        checkObjectIsAList(propertyName, propertyValue, item -> {
            if (!(item instanceof Integer)) {
                throw new PropertySetException("Expecting only identifiers inside the list for property " + propertyName);
            }
        });
    }

    public static void expectMapKey(Map<?, ?> map, String key, Class<?> clazz, String propertyName) throws PropertySetException {
        if (!map.containsKey(key) || (map.get(key) != null && !clazz.isAssignableFrom(map.get(key).getClass()))) {
            throw new PropertySetException("Expecting a " + key + " property of type " + clazz.getSimpleName() + " in " + propertyName);
        }
    }

    public static void expectMapKey(Map<?, ?> map, String key, Class<?> clazz, String propertyName, boolean isOptionalKey) throws PropertySetException {
        if ((!isOptionalKey && !map.containsKey(key)) || (map.get(key) != null && !clazz.isAssignableFrom(map.get(key).getClass()))) {
            throw new PropertySetException("Expecting a " + key + " property of type " + clazz.getSimpleName() + " in " + propertyName);
        }
    }

    public static void expectMapKey(Map<?, ?> map, String key, Class<?> clazz, int position, String propertyName) throws PropertySetException {
        if (!map.containsKey(key) || !map.get(key).getClass().equals(clazz)) {
            throw new PropertySetException("Expecting a " + key + " property of type " + clazz.getSimpleName() + " in position " + position + " of " + propertyName);
        }
    }

    @FunctionalInterface
    public interface CustomItemValidator {
        void validate(Object item) throws PropertySetException;
    }
}
