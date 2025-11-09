package io.pocketwatch.validators;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import io.pocketwatch.PocketWatch;
import io.pocketwatch.annotations.PlusDays;
import io.pocketwatch.annotations.ValidDate;
import io.pocketwatch.constants.DatePattern;
import io.pocketwatch.validator.FieldValidator;
import io.pocketwatch.validator.ValidationError;

public class PlusDaysValidator implements FieldValidator<PlusDays> {

	/**
	 * Validate field with @PlusDays annotation
	 */
	@Override
	public void validate(Object object, Field field, PlusDays annotation, List<ValidationError> errors) {
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
			Object value = field.get(object);
			if (value == null || value.toString().isBlank()) {
				return; // Field empty, skip
			}

			// Parse actual value
			PocketWatch actualDate = PocketWatch.parse(value.toString(), pattern);
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
				addError(errors, field, message, value);
			}

		} catch (NoSuchFieldException e) {
			addError(errors, field, "Source field '" + annotation.from() + "' not found", null);
		} catch (IllegalAccessException e) {
			addError(errors, field, "Cannot access field: " + e.getMessage(), null);
		}
	}

}
