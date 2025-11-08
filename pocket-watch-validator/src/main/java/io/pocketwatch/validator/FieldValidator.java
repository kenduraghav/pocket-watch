package io.pocketwatch.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

@FunctionalInterface
public interface FieldValidator<A extends Annotation> {

	 void validate(Object obj, Field field, A annotation, List<ValidationError> errors);
}
