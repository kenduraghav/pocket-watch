package io.pocketwatch.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface FutureDate {
	/**
     * Error message when validation fails.
     * Supports placeholders: {field}, {value}
     */
	String message()  default "{field} must be in the future";
	
	 /**
     * Whether to include the current date as valid (default: false).
     * If true, today's date is considered valid.
     * If false, only dates before today are valid.
     */
	boolean inclusive() default false;
}
