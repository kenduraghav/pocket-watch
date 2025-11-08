package io.pocketwatch.validator;

import static io.pocketwatch.annotations.constants.DatePattern.ISO_DATE;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import io.pocketwatch.PocketWatch;
import io.pocketwatch.annotations.DateRange;
import io.pocketwatch.annotations.FutureDate;
import io.pocketwatch.annotations.PastDate;
import io.pocketwatch.annotations.PlusDays;
import io.pocketwatch.annotations.ValidDate;
import io.pocketwatch.annotations.ValidTimeZone;
import io.pocketwatch.annotations.constants.DatePattern;
import io.pocketwatch.validators.ValidDateValidator;

/**
 * Central entry point for runtime validation.
 *
 * <p>Scans annotated fields and aggregates all ValidationErrors.</p>
 */
public final class PocketWatchValidator {
	
	private static final ValidatorRegistry registry = new ValidatorRegistry();
	
	
	static {
		registry.register(ValidDate.class, new ValidDateValidator());
	}

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
				ValidDate annotation = field.getAnnotation(ValidDate.class);
			    ValidDateValidator validator = (ValidDateValidator) registry.get(ValidDate.class);
			    validator.validate(object, field, annotation, errors);
			}

			// ADD THIS - Check if field has @PastDate annotation
			if (field.isAnnotationPresent(PastDate.class)) {
				validatePastDateField(object, field, errors);
			}


			// ADD THIS - Check if field has @FutureDate annotation
			if (field.isAnnotationPresent(FutureDate.class)) {
				validateFutureDateField(object, field, errors);
			}


			if(field.isAnnotationPresent(DateRange.class)) {
				validateDateRangeField(object, field, errors);
			}
			
			
			if (field.isAnnotationPresent(PlusDays.class)) {
			    validatePlusDaysField(object, field, errors);
			}
			
			if (field.isAnnotationPresent(ValidTimeZone.class)) {
			    validateTimeZone(object, field, errors);
			}
		}

		return ValidationResult.of(errors);
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
			LocalDate today = java.time.LocalDate.now();
			LocalDate fieldDate = parsed.toZonedDateTime().toLocalDate();

			boolean isValid = annotation.inclusive() 
					? !fieldDate.isAfter(today)  // Today or before
							: fieldDate.isBefore(today); // Only before today

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




	/**
	 * Validate a single field with @PastDate annotation
	 */
	private static void validateFutureDateField(Object object, Field field, List<ValidationError> errors) {
		FutureDate annotation = field.getAnnotation(FutureDate.class);
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

			// Check if date is in the future
			LocalDate today = java.time.LocalDate.now();
			LocalDate fieldDate = parsed.toZonedDateTime().toLocalDate();

			boolean isValid = annotation.inclusive() 
					? !fieldDate.isBefore(today)  // Today or after
							: fieldDate.isAfter(today); // Only after today

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



	private static void validateDateRangeField(Object object, Field field, List<ValidationError> errors) {
		DateRange annotation = field.getAnnotation(DateRange.class);
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
			if (field.isAnnotationPresent(DateRange.class)) {
				pattern = field.getAnnotation(DateRange.class).pattern();
			}

			// Parse the date
			PocketWatch parsed = PocketWatch.parse(dateString, pattern);
			if (parsed == null) {
				return; // Invalid date - @ValidDate will catch this
			}

			LocalDate today = LocalDate.now();
			LocalDate minDate = today.plusDays(annotation.minDaysFromNow());
			LocalDate maxDate = today.plusDays(annotation.maxDaysFromNow());
			LocalDate fieldDate = parsed.toZonedDateTime().toLocalDate();

			if(fieldDate.isBefore(minDate) || fieldDate.isAfter(maxDate)) {
				String message = annotation.message()
						.replace("{field}", fieldName)
						.replace("{min}", String.valueOf(annotation.minDaysFromNow()))
						.replace("{max}", String.valueOf(annotation.maxDaysFromNow()))
						.replace("{now}", today.format(DateTimeFormatter.ofPattern(DatePattern.SIMPLE_DATE)));
				errors.add(new ValidationError(fieldName, message, value));
			}


		}catch(IllegalAccessException e) {
			errors.add(new ValidationError(field.getName(), 
					"Cannot access field: " + e.getMessage(), null));
		}
	}
	
	
	/**
	 * Validate field with @PlusDays annotation
	 */
	private static void validatePlusDaysField(Object object, Field field, List<ValidationError> errors) {
	    PlusDays annotation = field.getAnnotation(PlusDays.class);
	    field.setAccessible(true);
	    
	    try {
	        String fieldName = field.getName();
	        String fromFieldName = annotation.from();
	        
	        // Get the source field
	        Field fromField = object.getClass().getDeclaredField(fromFieldName);
	        fromField.setAccessible(true);
	        Object fromValue = fromField.get(object);
	        
	        if (fromValue == null || fromValue.toString().isBlank()) {
	            return; // Source field empty, skip validation
	        }
	        
	        // Get pattern from source field's @ValidDate
	        String pattern = DatePattern.ISO_DATE;
	        if (fromField.isAnnotationPresent(ValidDate.class)) {
	            pattern = fromField.getAnnotation(ValidDate.class).pattern();
	        }
	        
	        // Parse source date and add days
	        PocketWatch fromDate = PocketWatch.parse(fromValue.toString(), pattern);
	        if (fromDate == null) {
	            return; // Invalid source date
	        }
	        
	        LocalDate expectedDate = fromDate.toZonedDateTime().toLocalDate()
	            .plusDays(annotation.days());
	        
	        // Get actual field value
	        Object actualValue = field.get(object);
	        if (actualValue == null || actualValue.toString().isBlank()) {
	            return; // Field empty, skip
	        }
	        
	        // Parse actual value
	        PocketWatch actualDate = PocketWatch.parse(actualValue.toString(), pattern);
	        if (actualDate == null) {
	            return; // Invalid format - @ValidDate handles this
	        }
	        
	        LocalDate actualLocalDate = actualDate.toZonedDateTime().toLocalDate();
	        
	        // Compare
	        if (!actualLocalDate.equals(expectedDate)) {
	            String message = annotation.message()
	                .replace("{field}", fieldName)
	                .replace("{from}", fromFieldName)
	                .replace("{days}", String.valueOf(annotation.days()));
	            errors.add(new ValidationError(fieldName, message, actualValue));
	        }
	        
	    } catch (NoSuchFieldException e) {
	        errors.add(new ValidationError(field.getName(), 
	            "Source field '" + annotation.from() + "' not found", null));
	    } catch (IllegalAccessException e) {
	        errors.add(new ValidationError(field.getName(), 
	            "Cannot access field", null));
	    }
	}
	
	
	private static void validateTimeZone(Object object, Field field, List<ValidationError> errors) {
		ValidTimeZone annotation = field.getAnnotation(ValidTimeZone.class);
		field.setAccessible(true);

		try {
			String fieldName = field.getName();
			boolean __isRequired__ = annotation.required();
			Object value = field.get(object);
			if(value == null  || value.toString().isBlank()) {
				 if (__isRequired__) {
			            String message = annotation.message()
			                .replace("{field}", fieldName);
			            errors.add(new ValidationError(fieldName, message, null));
			        }
			        return;
			}
			
			// Validate timezone
		    String timezoneId = value.toString();
		    try {
		        java.time.ZoneId.of(timezoneId);  // Throws if invalid
		    } catch (java.time.DateTimeException e) {
		        String message = annotation.message()
		            .replace("{field}", fieldName)
		            .replace("{value}", timezoneId);
		        errors.add(new ValidationError(fieldName, message, value));
		    }

		} catch (IllegalAccessException e) {
			errors.add(new ValidationError(field.getName(), 
			        "Cannot access field", null));
		}
	}

}
