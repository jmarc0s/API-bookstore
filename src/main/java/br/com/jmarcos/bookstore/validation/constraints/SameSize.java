package br.com.jmarcos.bookstore.validation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.jmarcos.bookstore.validation.SameSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SameSizeValidator.class)
public @interface SameSize {
    String message() default "Lists must have the same size";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String firstList();
    String secondList();
}

