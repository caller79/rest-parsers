package io.github.caller79.orderby.propertyacceptors;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by Carlos Aller on 10/01/20
 */
@lombok.RequiredArgsConstructor
public class ListOfPropertyNamesPropertyAcceptor implements PropertyAcceptor {

    private final String[] allowedProperties;

    @Override
    public boolean isPropertyAccepted(String name) {
        return ArrayUtils.contains(allowedProperties, name);
    }

    @Override
    public String describe() {
        return String.join(", ", allowedProperties);
    }
}
