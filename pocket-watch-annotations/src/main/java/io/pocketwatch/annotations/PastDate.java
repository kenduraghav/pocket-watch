package io.pocketwatch.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Validates that a date field is in the past.
 * Must be used together with @ValidDate.
 * 
 * Usage:
 * <pre>
 * {@literal @}ValidDate(pattern = DatePattern.ISO_DATE)
 * {@literal @}PastDate(message = "Birth date must be in the past")
 * private String birthDate;
 * </pre>
 * 
 * @author Raghav
 * @since 1.0.0
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface PastDate {

	/**
     * Error message when validation fails.
     * Supports placeholders: {field}, {value}
     */
	String message()  default "{field} must be in the past";
	
	 /**
     * Whether to include the current date as valid (default: false).
     * If true, today's date is considered valid.
     * If false, only dates before today are valid.
     */
	boolean inclusive() default false;
}
