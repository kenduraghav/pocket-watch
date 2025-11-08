package io.pocketwatch.validators;

import java.lang.reflect.Field;
import java.util.List;

import io.pocketwatch.annotations.ValidTimeZone;
import io.pocketwatch.validator.FieldValidator;
import io.pocketwatch.validator.ValidationError;

public class TimeZoneValidator implements FieldValidator<ValidTimeZone> {

	@Override
	public void validate(Object obj, Field field, ValidTimeZone annotation, List<ValidationError> errors) {
		field.setAccessible(true);

		try {
			String fieldName = field.getName();
			boolean __isRequired__ = annotation.required();
			Object value = field.get(obj);
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
