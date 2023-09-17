package com.example.tickets.validator.annotation;

import com.example.tickets.validator.RoleExistValidator;
import jakarta.validation.Constraint;
import org.springframework.messaging.handler.annotation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = RoleExistValidator.class)
@Target({ElementType.FIELD})
@Retention(RUNTIME)
public @interface RoleExist {

    String message() default "Invalid arguments";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
