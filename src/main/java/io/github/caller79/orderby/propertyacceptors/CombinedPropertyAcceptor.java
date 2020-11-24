package io.github.caller79.orderby.propertyacceptors;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Carlos Aller on 10/01/20
 */
public class CombinedPropertyAcceptor implements PropertyAcceptor {
    private final PropertyAcceptor[] acceptors;

    public CombinedPropertyAcceptor(PropertyAcceptor... acceptors) {
        this.acceptors = acceptors.clone();
    }

    @Override
    public boolean isPropertyAccepted(String name) {
        return Arrays.stream(acceptors).anyMatch(acceptor -> acceptor.isPropertyAccepted(name));
    }

    @Override
    public String describe() {
        return Arrays.stream(acceptors).map(PropertyAcceptor::describe).collect(Collectors.joining(", "));
    }
}
