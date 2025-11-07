package io.pocketwatch.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface PlusDays {

	
	 String message() default "{field} must be {days} days after {from}";
	
	/**
	 * Source field name
	 * @return
	 */
	String from();
	
	/**
	 * No of days to add
	 * @return
	 */
	int days();
}
