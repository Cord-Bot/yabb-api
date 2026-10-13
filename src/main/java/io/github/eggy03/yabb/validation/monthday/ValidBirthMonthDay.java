package io.github.eggy03.yabb.validation.monthday;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = BirthMonthDayValidator.class)
public @interface ValidBirthMonthDay {

    String message() default "BirthDate/Zone is invalid";

    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};
}
