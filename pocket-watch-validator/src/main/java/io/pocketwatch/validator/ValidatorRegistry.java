package io.pocketwatch.validator;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
 * Thread-safe registry mapping annotation types to their corresponding field
 * validators.
 *
 * <p>
 * Developers can register new validators dynamically at runtime, allowing for
 * easy extension of the PocketWatch validation framework.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * ValidatorRegistry registry = ValidatorRegistry.defaultRegistry();
 * registry.register(MyCustomDate.class, new MyCustomDateValidator());
 * </pre>
 *
 * @author Raghav
 * @since 1.0.0
 */
public class ValidatorRegistry {

	private final Map<Class<? extends Annotation>, FieldValidator<?>> validators = new ConcurrentHashMap<>();

	/**
	 * Returns a registry pre-populated with all built-in PocketWatch validators.
	 *
	 * @return a registry containing the standard annotation mappings
	 */
	public static ValidatorRegistry defaultRegistry() {
		ValidatorRegistry registry = new ValidatorRegistry();
		registry.register(ValidDate.class, new ValidDateValidator());
		registry.register(PastDate.class, new PastDateValidator());
		registry.register(FutureDate.class, new FutureDateValidator());
		registry.register(DateRange.class, new DateRangeValidator());
		registry.register(PlusDays.class, new PlusDaysValidator());
		registry.register(ValidTimeZone.class, new TimeZoneValidator());
		return registry;
	}

	/**
	 * Registers a validator for a given annotation type. If a validator already
	 * exists, it will be replaced.
	 *
	 * @param annotationType the annotation type
	 * @param fieldValidator the validator implementation
	 * @param <A>            the annotation subtype
	 */
	public <A extends Annotation> void register(Class<A> annotationType, FieldValidator<A> fieldValidator) {
		validators.put(annotationType, fieldValidator);
	}

	/**
	 * Retrieves the validator associated with the given annotation type.
	 *
	 * @param annotationType the annotation type
	 * @param <A>            the annotation subtype
	 * @return the validator, or {@code null} if not registered
	 */
	@SuppressWarnings("unchecked")
	public  <A extends Annotation> FieldValidator<A> get(Class<? extends Annotation> annotationType) {
		return (FieldValidator<A>) validators.get(annotationType);
	}

	/**
	 * Checks if a validator exists for a given annotation type.
	 *
	 * @param annotationType the annotation type
	 * @return true if a validator is registered, false otherwise
	 */
	public boolean hasValidator(Class<? extends Annotation> annotationType) {
		return validators.containsKey(annotationType);
	}
}
