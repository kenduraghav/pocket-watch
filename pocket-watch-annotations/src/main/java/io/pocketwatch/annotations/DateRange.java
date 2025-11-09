package io.pocketwatch.annotations;


import static io.pocketwatch.constants.DatePattern.ISO_DATE;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface DateRange {
    
    String message() default "{field} must be between {min} and {max} days from {now}";
    
    int minDaysFromNow() default 0;
    
    int maxDaysFromNow() default 365;
    
    /**
     * Date pattern to validate against.
     * Use DatePattern constants or custom pattern.
     */
    String pattern() default ISO_DATE;
    
}