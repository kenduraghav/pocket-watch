package io.pocketwatch.validators;

import static io.pocketwatch.constants.DatePattern.ISO_DATE;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.pocketwatch.PocketWatch;
import io.pocketwatch.annotations.DateRange;
import io.pocketwatch.constants.DatePattern;
import io.pocketwatch.validator.FieldValidator;
import io.pocketwatch.validator.ValidationError;

public class DateRangeValidator implements FieldValidator<DateRange>{

	@Override
	public void validate(Object obj, Field field, DateRange annotation, List<ValidationError> errors) {
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
				addError(errors, field, message, value);
			}


		}catch(IllegalAccessException e) {
			addError(errors, field, "Cannot access field: " + e.getMessage(), null);
		}
	}

}
