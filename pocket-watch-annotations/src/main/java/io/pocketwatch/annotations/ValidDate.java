package io.pocketwatch.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Validates that a String field contains a valid date in the specified format.
 * 
 * Usage:
 * <pre>
 * public class Event {
 *     {@literal @}ValidDate(pattern = DatePattern.ISO_DATE)
 *     private String startDate;
 * }
 * </pre>
 * 
 * @author Raghav
 * @since 1.0.0
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface ValidDate {
	
	 /**
     * Error message when validation fails.
     * Supports placeholders: {field}, {pattern}, {value}
     */
    String message() default "{field} must be a valid date in format {pattern}";
    
    /**
     * Date pattern to validate against.
     * Use DatePattern constants or custom pattern.
     */
    String pattern() default DatePattern.ISO_DATE;
    
    /**
     * Whether the field is required (non-null, non-empty).
     */
    boolean required() default true;
}
