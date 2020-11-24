package io.github.caller79.propertysetters;

public class PropertySetException extends Exception {
    public PropertySetException() {
        // Empty
    }

    public PropertySetException(String message) {
        super(message);
    }

    public PropertySetException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertySetException(Throwable cause) {
        super(cause);
    }

    public PropertySetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
