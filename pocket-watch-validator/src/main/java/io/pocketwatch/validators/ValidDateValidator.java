package io.pocketwatch.validators;

import java.lang.reflect.Field;
import java.util.List;

import io.pocketwatch.PocketWatch;
import io.pocketwatch.annotations.ValidDate;
import io.pocketwatch.validator.FieldValidator;
import io.pocketwatch.validator.ValidationError;

public class ValidDateValidator implements FieldValidator<ValidDate> {

	@Override
	public void validate(Object obj, Field field, ValidDate annotation, List<ValidationError> errors) {
		try {
			Object value = field.get(obj);
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
				addError(errors, field, message, value);
			}

		} catch (IllegalAccessException e) {
			addError(errors, field, "Cannot access field: " + e.getMessage(), null);
		}
	}

}
