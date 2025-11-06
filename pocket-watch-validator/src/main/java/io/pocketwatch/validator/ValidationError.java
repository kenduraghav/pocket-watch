package io.pocketwatch.validator;

import java.util.Objects;

/**
 * Represents a single field validation failure.
 *
 * @author PocketWatch
 * @since 1.0.0
 */
public final class ValidationError {

    private final String fieldName;
    private final String message;
    private final Object rejectedValue;

    public ValidationError(String fieldName, String message, Object rejectedValue) {
        this.fieldName = Objects.requireNonNull(fieldName, "fieldName must not be null");
        this.message = Objects.requireNonNull(message, "message must not be null");
        this.rejectedValue = rejectedValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    @Override
    public String toString() {
        return fieldName + ": " + message + " (value: " + rejectedValue + ")";
    }
}
