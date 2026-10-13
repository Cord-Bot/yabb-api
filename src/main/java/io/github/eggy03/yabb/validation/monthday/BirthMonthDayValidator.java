package io.github.eggy03.yabb.validation.monthday;

import io.github.eggy03.yabb.dto.BirthdayRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.MonthDay;
import java.time.ZoneId;

public class BirthMonthDayValidator implements ConstraintValidator<ValidBirthMonthDay, BirthdayRequestDTO> {

    @Override
    @SuppressWarnings("java:S2201")
    public boolean isValid(BirthdayRequestDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getDay() == null || dto.getMonth() == null)
            return true; // let inbuilt @NotNull placed in the DTO handle null validations

        try {
            MonthDay.of(dto.getMonth(), dto.getDay());
            ZoneId.of(dto.getZoneId());
            return true;
        }  catch (DateTimeException e) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(e.getMessage()).addConstraintViolation();
                return false;
        }
    }
}
