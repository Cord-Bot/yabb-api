package io.github.eggy03.yabb.validation.zone;

import io.github.eggy03.yabb.dto.BirthdayDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.ZoneId;

public class TimeZoneValidator implements ConstraintValidator<ValidTimeZone, BirthdayDTO> {

    @Override
    @SuppressWarnings("java:S2201")
    public boolean isValid(BirthdayDTO dto, ConstraintValidatorContext context) {

        if (dto == null || dto.getZoneId() == null)
            return true; // let inbuilt @NotNull placed in the DTO handle null validations

        try {
            ZoneId.of(dto.getZoneId());
            return true;
        }  catch (DateTimeException e) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(e.getMessage()).addConstraintViolation();
                return false;
        }
    }
}
