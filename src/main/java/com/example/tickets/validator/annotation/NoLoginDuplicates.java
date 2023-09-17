package com.example.tickets.validator.annotation;

import com.example.tickets.validator.LoginValidator;
import jakarta.validation.Constraint;
import org.springframework.messaging.handler.annotation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = LoginValidator.class)
@Target({ElementType.FIELD})
@Retention(RUNTIME)
public @interface NoLoginDuplicates {

    String message() default "Invalid arguments";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
