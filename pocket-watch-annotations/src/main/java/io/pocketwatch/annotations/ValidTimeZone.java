package io.pocketwatch.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ValidTimeZone {
    
    String message() default "{field} must be a valid timezone";
    
    /**
     * Whether field is required (non-null, non-empty)
     */
    boolean required() default true;
}