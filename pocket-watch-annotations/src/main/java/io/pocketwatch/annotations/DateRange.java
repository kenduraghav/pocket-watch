package io.pocketwatch.annotations;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface DateRange {
    
    String message() default "{field} must be between {min} and {max} days from {value}";
    
    int minDaysFromNow() default 0;
    
    int maxDaysFromNow() default 365;
    
    	
}