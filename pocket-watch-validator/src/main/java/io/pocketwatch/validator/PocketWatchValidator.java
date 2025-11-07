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
import io.pocketwatch.annotations.constants.DatePattern;

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
}
