package io.pocketwatch.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

@FunctionalInterface
public interface FieldValidator<A extends Annotation> {
	
	 /**
     * Validates the given field of the specified object using the provided annotation.
     *
     * @param obj the object being validated
     * @param field the field to validate
     * @param annotation the annotation instance present on the field
     * @param errors a mutable list to collect validation errors
     */
	void validate(Object obj, Field field, A annotation, List<ValidationError> errors);

	/**
	 * Helper for adding validation errors in concrete validators.
	 *
	 * @param errors        the error list
	 * @param field         the field being validated
	 * @param message       the error message
	 * @param rejectedValue the invalid value
	 */
	default void addError(List<ValidationError> errors, Field field, String message, Object rejectedValue) {
		errors.add(new ValidationError(field.getName(), message, rejectedValue));
	}
}
