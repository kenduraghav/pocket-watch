package io.pocketwatch.validator;

import java.lang.reflect.Field;
import java.util.*;

import io.pocketwatch.PocketWatch;
import io.pocketwatch.annotations.ValidDate;

/**
 * Central entry point for runtime validation.
 *
 * <p>Scans annotated fields and aggregates all ValidationErrors.</p>
 */
public final class PocketWatchValidator {

	 public static ValidationResult validate(Object object) {
	        if (object == null) {
	            throw new IllegalArgumentException("Object to validate cannot be null");
	        }
	        
	        List<ValidationError> errors = new ArrayList<>();
	        
	        // Get all fields from the class
	        Field[] fields = object.getClass().getDeclaredFields();
	        
	        for (Field field : fields) {
	            // Check if field has @ValidDate annotation
	            if (field.isAnnotationPresent(ValidDate.class)) {
	                validateDateField(object, field, errors);
	            }
	        }
	        
	        return ValidationResult.of(errors);
	    }
	    
	    /**
	     * Validate a single field with @ValidDate annotation
	     */
	    private static void validateDateField(Object object, Field field, List<ValidationError> errors) {
	        ValidDate annotation = field.getAnnotation(ValidDate.class);
	        field.setAccessible(true);
	        
	        try {
	            Object value = field.get(object);
	            String fieldName = field.getName();
	            
	            // Check if required
	            if (annotation.required() && (value == null || value.toString().isBlank())) {
	                String message = annotation.message()
	                    .replace("{field}", fieldName)
	                    .replace("{pattern}", annotation.pattern())
	                    .replace("{value}", "null");
	                errors.add(new ValidationError(fieldName, message, null));
	                return;
	            }
	            
	            // Skip if not required and null
	            if (!annotation.required() && value == null) {
	                return;
	            }
	            
	            // Try to parse with pattern
	            String dateString = value.toString();
	            PocketWatch parsed = PocketWatch.parse(dateString, annotation.pattern());
	            
	            if (parsed == null) {
	                String message = annotation.message()
	                    .replace("{field}", fieldName)
	                    .replace("{pattern}", annotation.pattern())
	                    .replace("{value}", dateString);
	                errors.add(new ValidationError(fieldName, message, value));
	            }
	            
	        } catch (IllegalAccessException e) {
	            errors.add(new ValidationError(field.getName(), 
	                "Cannot access field: " + e.getMessage(), null));
	        }
	    }
    
}
