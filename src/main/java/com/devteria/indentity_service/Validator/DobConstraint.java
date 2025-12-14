package com.devteria.indentity_service.Validator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = {DobValidator.class})
public @interface DobConstraint {
	String message() default "";
	
	int min();
	
	
	Class<?>[] groups() default{};
	
	Class<? extends Payload>[] payload() default {};
	
	

}
