package io.github.eggy03.yabb.validation.zone;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = TimeZoneValidator.class)
public @interface ValidTimeZone {

    String message() default "BirthDate/Zone is invalid";

    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};
}
