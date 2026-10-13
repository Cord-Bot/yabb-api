package io.github.eggy03.yabb.validation.zone;

import io.github.eggy03.yabb.dto.BirthdayRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.ZoneId;

public class TimeZoneValidator implements ConstraintValidator<ValidTimeZone, BirthdayRequestDTO> {

    @Override
    @SuppressWarnings("java:S2201")
    public boolean isValid(BirthdayRequestDTO dto, ConstraintValidatorContext context) {

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
