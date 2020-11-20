package caller79.propertysetters;

/**
 * Created by Carlos Aller on 9/10/19
 */
@ItemPropertySetterImplementations(
    classes = {
        DatePropertySetter.class,
        IntegerPropertySetter.class,
        LocalDateTimePropertySetter.class,
        BooleanPropertySetter.class,
        PrimitiveBooleanPropertySetter.class,
        EnumAsStringPropertySetter.class,
        BeanPropertySetter.class
    })
@lombok.Data
public abstract class ItemPropertySetter<T> {
    private ItemPropertySetterInitializer itemPropertySetterInitializer;

    public abstract void setProperty(T item, String propertyName, Object propertyValue) throws PropertySetException;

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public void validate(String propertyName, Object propertyValue) throws PropertySetException {
        // By default, no validation, but subclasses may want to throw exceptions if the value is not acceptable for the
    }
}
