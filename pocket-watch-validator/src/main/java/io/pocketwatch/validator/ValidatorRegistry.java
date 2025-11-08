package io.pocketwatch.validator;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class ValidatorRegistry {

	private final Map<Class<? extends Annotation>, FieldValidator<?>> validators = new HashMap<>();

	public <A extends Annotation> void register(Class<A> annotationType, FieldValidator<A> fieldValidator) {
		validators.put(annotationType, fieldValidator);
	}

	public FieldValidator<?> get(Class<? extends Annotation> annotationType) {
		return validators.get(annotationType);
	}

	public boolean hasValidator(Class<? extends Annotation> annotationType) {
		return validators.containsKey(annotationType);
	}
}
