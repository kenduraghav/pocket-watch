package io.pocketwatch.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.pocketwatch.annotations.DateRange;
import io.pocketwatch.annotations.FutureDate;
import io.pocketwatch.annotations.PastDate;
import io.pocketwatch.annotations.PlusDays;
import io.pocketwatch.annotations.ValidDate;
import io.pocketwatch.annotations.ValidTimeZone;
import io.pocketwatch.validators.DateRangeValidator;
import io.pocketwatch.validators.FutureDateValidator;
import io.pocketwatch.validators.PastDateValidator;
import io.pocketwatch.validators.PlusDaysValidator;
import io.pocketwatch.validators.TimeZoneValidator;
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
		registry.register(PastDate.class, new PastDateValidator());
		registry.register(FutureDate.class, new FutureDateValidator());
		registry.register(DateRange.class, new DateRangeValidator());
		registry.register(PlusDays.class, new PlusDaysValidator());
		registry.register(ValidTimeZone.class, new TimeZoneValidator());
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
				PastDate annotation = field.getAnnotation(PastDate.class);
				PastDateValidator validator = (PastDateValidator) registry.get(PastDate.class);
				validator.validate(object, field, annotation, errors);
			}


			// ADD THIS - Check if field has @FutureDate annotation
			if (field.isAnnotationPresent(FutureDate.class)) {
				FutureDate annotation = field.getAnnotation(FutureDate.class);
				FutureDateValidator validator = (FutureDateValidator) registry.get(FutureDate.class);
				validator.validate(object, field, annotation, errors);
			}


			if(field.isAnnotationPresent(DateRange.class)) {
				DateRange annotation = field.getAnnotation(DateRange.class);
				DateRangeValidator validator = (DateRangeValidator) registry.get(DateRange.class);
				validator.validate(object, field, annotation, errors);
			}


			if (field.isAnnotationPresent(PlusDays.class)) {
				PlusDays annotation = field.getAnnotation(PlusDays.class);
				PlusDaysValidator validator = (PlusDaysValidator) registry.get(PlusDays.class);
				validator.validate(object, field, annotation, errors);
			}

			if (field.isAnnotationPresent(ValidTimeZone.class)) {
				ValidTimeZone annotation = field.getAnnotation(ValidTimeZone.class);
				TimeZoneValidator validator = (TimeZoneValidator) registry.get(ValidTimeZone.class);
				validator.validate(object, field, annotation, errors);
			}
		}

		return ValidationResult.of(errors);
	}

}
