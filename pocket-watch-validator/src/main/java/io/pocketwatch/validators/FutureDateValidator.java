package io.pocketwatch.validators;

import static io.pocketwatch.constants.DatePattern.ISO_DATE;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import io.pocketwatch.PocketWatch;
import io.pocketwatch.annotations.FutureDate;
import io.pocketwatch.annotations.ValidDate;
import io.pocketwatch.validator.FieldValidator;
import io.pocketwatch.validator.ValidationError;

public class FutureDateValidator implements FieldValidator<FutureDate> {

	@Override
	public void validate(Object obj, Field field, FutureDate annotation, List<ValidationError> errors) {
		try {
			Object value = field.get(obj);

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
				addError(errors, field, message, value);
			}

		} catch (IllegalAccessException e) {
			addError(errors, field, "Cannot access field: " + e.getMessage(), null);
		}
	}

}
