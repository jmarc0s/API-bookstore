package br.com.jmarcos.bookstore.validation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.jmarcos.bookstore.validation.NotRepeatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotRepeatValidator.class)
public @interface NotRepeat {
    String message() default "Ids must not repeate";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
