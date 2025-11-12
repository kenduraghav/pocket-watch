package io.pocketwatch.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central entry point for runtime validation.
 *
 * <p>Scans annotated fields and aggregates all ValidationErrors.</p>
 */
public final class PocketWatchValidator {

	private static final ValidatorRegistry registry  = ValidatorRegistry.defaultRegistry();
	
	  // Field cache for performance
    private static final Map<Class<?>, Field[]> FIELD_CACHE = new ConcurrentHashMap<>();
    
    /**
     * Validates an object using all registered validators in the registry.
     *
     * @param target the object to validate
     * @return a ValidationResult containing success or failure with errors
     */
	public static ValidationResult validate(Object object) {
		
		if(object== null) {
			 throw new IllegalArgumentException("Object to validate cannot be null" + object);
		}

		List<ValidationError> errors = new ArrayList<>();

		// Get all fields from the class
		Field[] fields = getFields(object.getClass());

		for (Field field : fields) {
			for (Annotation annotation : field.getAnnotations()) {
			    FieldValidator<Annotation> validator = registry.get(annotation.annotationType());
			    if (validator != null) {
			        validator.validate(object, field, annotation, errors);
			    }
			}
		}
		
		return ValidationResult.of(errors);
	}
	
	
	 /**
     * Retrieves cached fields for the given class.
     * Ensures accessible reflection objects and avoids redundant lookups.
     *
     * @param type the target class
     * @return array of declared fields (cached)
     */
    private static Field[] getFields(Class<?> type) {
        return FIELD_CACHE.computeIfAbsent(type, cls -> {
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) f.setAccessible(true);
            return fields;
        });
    }
}
