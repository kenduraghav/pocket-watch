package io.pocketwatch.annotations;

import static io.pocketwatch.annotations.constants.DatePattern.ISO_DATE;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface DateRange {
    
    String message() default "{field} must be between {min} and {max} days from now";
    
    int minDaysFromNow() default 0;
    
    int maxDaysFromNow() default 365;
    
    /**
     * Date pattern to validate against.
     * Use DatePattern constants or custom pattern.
     */
    String pattern() default ISO_DATE;
    
}