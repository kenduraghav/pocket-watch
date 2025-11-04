package io.pocketwatch.validator;

import java.util.Collections;
import java.util.List;
import java.util.*;

/**
 * Immutable result of a validation operation.
 *
 * @author PocketWatch
 * @since 1.0.0
 */
public final class ValidationResult {

private final List<ValidationError> errors;
    
    private ValidationResult(List<ValidationError> errors) {
        this.errors = Collections.unmodifiableList(errors);
    }
    
    public static ValidationResult success() {
        return new ValidationResult(Collections.emptyList());
    }
    
    public static ValidationResult of(List<ValidationError> errors) {
        return new ValidationResult(errors);
    }
    
    public boolean isValid() {
        return errors.isEmpty();
    }
    
    public List<ValidationError> getErrors() {
        return errors;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
