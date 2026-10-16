package io.github.eggy03.yabb.validation.monthday;

import io.github.eggy03.yabb.dto.BirthdayDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.MonthDay;
import java.time.ZoneId;

public class BirthMonthDayValidator implements ConstraintValidator<ValidBirthMonthDay, BirthdayDTO> {

    @Override
    @SuppressWarnings("java:S2201")
    public boolean isValid(BirthdayDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getDay() == null || dto.getMonth() == null || dto.getZoneId() == null)
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
