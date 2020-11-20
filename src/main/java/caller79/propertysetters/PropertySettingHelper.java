package caller79.propertysetters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Carlos Aller on 9/10/19
 */
@Slf4j
public final class PropertySettingHelper<T> {

    private final ItemPropertySetterInitializer propertySetterInitializer;
    private final PropertySettingOptions propertySettingOptions;

    public PropertySettingHelper(ItemPropertySetterInitializer propertySetterInitializer) {
        this(propertySetterInitializer, PropertySettingOptions.builder().failOnUnknownProperties(true).build());
    }

    public PropertySettingHelper(ItemPropertySetterInitializer propertySetterInitializer, PropertySettingOptions propertySettingOptions) {
        this.propertySetterInitializer = propertySetterInitializer;
        this.propertySettingOptions = propertySettingOptions;
    }

    private Map<String, PropertySettingInfo<T>> getWritableProperties(Class<T> clazz) {
        Map<String, PropertySettingInfo<T>> props = new LinkedHashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        SetteableFromPropertyRequest classAnnotation = clazz.getAnnotation(SetteableFromPropertyRequest.class);
        boolean allFieldsByDefault = classAnnotation != null && !classAnnotation.ignore();
        for (Field field : fields) {
            SetteableFromPropertyRequest annotation = field.getAnnotation(SetteableFromPropertyRequest.class);
            if ((allFieldsByDefault && (annotation == null || !annotation.ignore())) || (annotation != null && !annotation.ignore())) {
                addPropertySetterForField(props, field, annotation == null ? classAnnotation : annotation);
            }
        }
        // Check parent class annotation
        SetteableVirtualProperties classVirtualPropertiesAnnotation = clazz.getAnnotation(SetteableVirtualProperties.class);
        if (classVirtualPropertiesAnnotation != null) {
            SetteableFromPropertyRequest[] properties = classVirtualPropertiesAnnotation.properties();
            Arrays.stream(properties).forEach(property -> addInstanceOfPropertySetter(props, StringUtils.defaultString(property.name()), property.setter(), property.priority()));
        }

        return Collections.unmodifiableMap(props);
    }

    private void addPropertySetterForField(Map<String, PropertySettingInfo<T>> props, Field field, SetteableFromPropertyRequest annotation) {
        String propertyName = annotation.name();
        if (StringUtils.isEmpty(propertyName)) {
            propertyName = field.getName();
        }
        Class<? extends ItemPropertySetter> annotationSetterClass = annotation.setter();
        if (ItemPropertySetter.class.equals(annotationSetterClass)) {
            findAdequateSetterImplementationAndAddIt(props, field, propertyName, annotation.priority());
        } else {
            addInstanceOfPropertySetter(props, propertyName, annotationSetterClass, annotation.priority());
        }
    }

    private void findAdequateSetterImplementationAndAddIt(Map<String, PropertySettingInfo<T>> props, Field field, String propertyName, int priority) {
        // Find the most adequate type among all registered implementations
        ItemPropertySetterImplementations implementations = ItemPropertySetter.class.getAnnotation(ItemPropertySetterImplementations.class);
        Class<? extends ItemPropertySetter>[] implementationClasses = implementations.classes();
        for (Class<? extends ItemPropertySetter> implementationClass : implementationClasses) {
            SupportedTypes supportedTypes = implementationClass.getAnnotation(SupportedTypes.class);
            if (supportedTypes != null) {
                Class<?>[] supportedClasses = supportedTypes.classes();
                for (Class<?> aClass : supportedClasses) {
                    if (aClass.isAssignableFrom(field.getType())) {
                        addInstanceOfPropertySetter(props, propertyName, implementationClass, priority);
                        return;
                    }
                }
            }
        }
    }

    private void addInstanceOfPropertySetter(Map<String, PropertySettingInfo<T>> props, String propertyName, Class<? extends ItemPropertySetter> annotationSetterClass, int priority) {
        try {
            ItemPropertySetter<T> instance = annotationSetterClass.newInstance();
            if (propertySetterInitializer != null) {
                instance.setItemPropertySetterInitializer(propertySetterInitializer);
                propertySetterInitializer.initialize(instance);
            }
            props.put(propertyName, PropertySettingInfo.<T>builder().propertySetter(instance).priority(priority).build());
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Initialization error: ", e);
        }
    }

    public void applyProperties(T target, List<PropertyRequest> properties) throws PropertySetException {
        List<BusinessExceptionRunnable> operationsToDo = new ArrayList<>();
        Map<String, PropertySettingInfo<T>> writableProperties = getWritableProperties((Class<T>) target.getClass());
        if (properties != null) {
            for (PropertyRequest propertyRequest : properties) {
                PropertySettingInfo<T> info = writableProperties.get(propertyRequest.getName());
                validateInfoAndFail(writableProperties, info);
                if (info != null) {
                    ItemPropertySetter<T> setter = info.getPropertySetter();
                    setter.validate(propertyRequest.getName(), propertyRequest.getValue());
                    operationsToDo.add(new BusinessExceptionRunnable() {
                        @Override
                        public int priority() {
                            return info.getPriority();
                        }

                        @Override
                        public String propertyName() {
                            return propertyRequest.getName();
                        }

                        @Override
                        public void run() throws PropertySetException {
                            setter.setProperty(target, propertyRequest.getName(), propertyRequest.getValue());
                        }
                    });
                }
            }
        }
        operationsToDo.sort(Comparator.comparingInt(BusinessExceptionRunnable::priority).thenComparing(BusinessExceptionRunnable::propertyName));
        for (BusinessExceptionRunnable businessExceptionRunnable : operationsToDo) {
            businessExceptionRunnable.run();
        }
    }

    private void validateInfoAndFail(Map<String, PropertySettingInfo<T>> writableProperties, PropertySettingInfo<T> info) throws PropertySetException {
        if (info == null || info.getPropertySetter() == null) {
            List<String> sortedProperties = new ArrayList<>(writableProperties.keySet());
            sortedProperties.sort(String::compareTo);
            String message = "Cannot set that property, only these are allowed: " + sortedProperties;
            if (propertySettingOptions.isFailOnUnknownProperties()) {
                throw new PropertySetException(message);
            } else {
                log.warn(message);
            }
        }
    }

    private interface BusinessExceptionRunnable {
        int priority();

        String propertyName();

        void run() throws PropertySetException;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    private static class PropertySettingInfo<T> {
        ItemPropertySetter<T> propertySetter;
        int priority;
    }

    @lombok.Data
    @lombok.Builder
    public static class PropertySettingOptions {
        private boolean failOnUnknownProperties;
    }
}
