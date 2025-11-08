package io.pocketwatch.validator;

import java.util.Objects;

/**
 * Represents a single field validation failure.
 *
 * @author PocketWatch
 * @since 1.0.0
 */
public record ValidationError(String fieldName, String message, Object rejectedValue) {
	
	public ValidationError {
        fieldName = Objects.requireNonNull(fieldName, "fieldName must not be null");
        message = Objects.requireNonNull(message, "message must not be null");
    }

    @Override
    public String toString() {
        return fieldName + ": " + message + " (value: " + rejectedValue + ")";
    }
}