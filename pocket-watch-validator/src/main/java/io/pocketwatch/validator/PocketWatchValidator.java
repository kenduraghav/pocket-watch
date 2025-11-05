package io.pocketwatch.validator;

import static io.pocketwatch.annotations.DatePattern.*;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import io.pocketwatch.PocketWatch;
import io.pocketwatch.annotations.PastDate;
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
	            
	         // ADD THIS - Check if field has @PastDate annotation
	            if (field.isAnnotationPresent(PastDate.class)) {
	                validatePastDateField(object, field, errors);
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
	    
	    
	    
	    /**
	     * Validate a single field with @PastDate annotation
	     */
	    private static void validatePastDateField(Object object, Field field, List<ValidationError> errors) {
	        PastDate annotation = field.getAnnotation(PastDate.class);
	        field.setAccessible(true);
	        
	        try {
	            Object value = field.get(object);
	            
	            // Skip if null (let @ValidDate handle required check)
	            if (value == null || value.toString().isBlank()) {
	                return;
	            }
	            
	            String fieldName = field.getName();
	            String dateString = value.toString();
	            
	            // Get pattern from @ValidDate if present
	            String pattern = ISO_DATE;
	            if (field.isAnnotationPresent(ValidDate.class)) {
	                pattern = field.getAnnotation(ValidDate.class).pattern();
	            }
	            
	            // Parse the date
	            PocketWatch parsed = PocketWatch.parse(dateString, pattern);
	            if (parsed == null) {
	                return; // Invalid date - @ValidDate will catch this
	            }
	            
	            // Check if date is in the past
	            ZonedDateTime now = ZonedDateTime.now();
	            ZonedDateTime fieldDate = parsed.toZonedDateTime();
	            
	            boolean isValid = annotation.inclusive() 
	                ? !fieldDate.isAfter(now)  // Today or before
	                : fieldDate.isBefore(now); // Only before today
	            
	            if (!isValid) {
	                String message = annotation.message()
	                    .replace("{field}", fieldName)
	                    .replace("{value}", dateString);
	                errors.add(new ValidationError(fieldName, message, value));
	            }
	            
	        } catch (IllegalAccessException e) {
	            errors.add(new ValidationError(field.getName(), 
	                "Cannot access field: " + e.getMessage(), null));
	        }
	    }
    
}
