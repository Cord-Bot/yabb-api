package io.github.eggy03.yabb.utility;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class BirthdayScheduleCalculatorTest {

    private BirthdayScheduleCalculator calculator;

    // freeze the time cause this method depends on the clock
    @Test
    void calculateUpcomingBirthday_whenBirthdayIsToday() {
        Instant today = LocalDateTime
                .of(2001, Month.JULY, 6, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.fixed(today, ZoneOffset.UTC));

        Instant upcomingBirthday = calculator.calculateUpcomingBirthday(6, 7, "Asia/Kolkata");

        assertThat(upcomingBirthday).isEqualTo(today);
    }

    @Test
    void calculateUpcomingBirthday_whenBirthdayIsTomorrow() {
        Instant today = LocalDateTime
                .of(2001, Month.JULY, 6, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.fixed(today, ZoneOffset.UTC));

        Instant upcomingBirthday = calculator.calculateUpcomingBirthday(7, 7, "Asia/Kolkata");

        Instant expected = LocalDateTime
                .of(2001, Month.JULY, 7, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        assertThat(upcomingBirthday).isEqualTo(expected);
    }

    @Test
    void calculateUpcomingBirthday_whenBirthdayWasYesterday() {
        Instant today = LocalDateTime
                .of(2001, Month.JULY, 6, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.fixed(today, ZoneOffset.UTC));

        Instant upcomingBirthday = calculator.calculateUpcomingBirthday(5, 7, "Asia/Kolkata");

        Instant expected = LocalDateTime
                .of(2002, Month.JULY, 5, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        assertThat(upcomingBirthday).isEqualTo(expected);
    }

    @Test
    void calculateUpcomingBirthday_whenBirthdayIsLaterInTheYear() {
        Instant today = LocalDateTime
                .of(2001, Month.JANUARY, 1, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.fixed(today, ZoneOffset.UTC));

        Instant upcomingBirthday = calculator.calculateUpcomingBirthday(31, 12, "Asia/Kolkata");

        Instant expected = LocalDateTime
                .of(2001, Month.DECEMBER, 31, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        assertThat(upcomingBirthday).isEqualTo(expected);
    }

    @Test
    void calculateUpcomingBirthday_whenBirthdayIsNextYear() {
        Instant today = LocalDateTime
                .of(2001, Month.DECEMBER, 31, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.fixed(today, ZoneOffset.UTC));

        Instant upcomingBirthday = calculator.calculateUpcomingBirthday(1, 1, "Asia/Kolkata");

        Instant expected = LocalDateTime
                .of(2002, Month.JANUARY, 1, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        assertThat(upcomingBirthday).isEqualTo(expected);
    }

    @Test
    void calculateUpcomingBirthday_whenLeapDayBirthdayIsTodayInLeapYear() {
        Instant today = LocalDateTime
                .of(2000, Month.FEBRUARY, 29, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.fixed(today, ZoneOffset.UTC));

        Instant upcomingBirthday = calculator.calculateUpcomingBirthday(29, 2, "Asia/Kolkata");

        assertThat(upcomingBirthday).isEqualTo(today);
    }

    @Test
    void calculateUpcomingBirthday_whenLeapDayBirthdayIsTomorrow() {
        Instant today = LocalDateTime
                .of(2000, Month.FEBRUARY, 28, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.fixed(today, ZoneOffset.UTC));

        Instant upcomingBirthday = calculator.calculateUpcomingBirthday(29, 2, "Asia/Kolkata");

        Instant expected = LocalDateTime
                .of(2000, Month.FEBRUARY, 29, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        assertThat(upcomingBirthday).isEqualTo(expected);
    }

    @Test
    void calculateUpcomingBirthday_whenLeapDayFallsOnFebruary28InNonLeapYear() {
        Instant today = LocalDateTime
                .of(2001, Month.FEBRUARY, 28, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.fixed(today, ZoneOffset.UTC));

        Instant upcomingBirthday = calculator.calculateUpcomingBirthday(29, 2, "Asia/Kolkata");

        assertThat(upcomingBirthday).isEqualTo(today);
    }

    @Test
    void calculateUpcomingBirthday_whenLeapDayBirthdayHasPassedInNonLeapYear() {
        Instant today = LocalDateTime
                .of(2001, Month.MARCH, 1, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.fixed(today, ZoneOffset.UTC));

        Instant upcomingBirthday = calculator.calculateUpcomingBirthday(29, 2, "Asia/Kolkata");

        Instant expected = LocalDateTime
                .of(2002, Month.FEBRUARY, 28, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        assertThat(upcomingBirthday).isEqualTo(expected);
    }

    @Test
    void calculateUpcomingBirthday_whenNextYearIsLeapYear() {
        Instant today = LocalDateTime
                .of(1999, Month.MARCH, 1, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.fixed(today, ZoneOffset.UTC));

        Instant upcomingBirthday = calculator.calculateUpcomingBirthday(29, 2, "Asia/Kolkata");

        Instant expected = LocalDateTime
                .of(2000, Month.FEBRUARY, 29, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        assertThat(upcomingBirthday).isEqualTo(expected);
    }

    @Test
    void calculateUpcomingBirthday_whenFebruary28BirthdayHasPassedOnLeapDay() {
        Instant today = LocalDateTime
                .of(2000, Month.FEBRUARY, 29, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.fixed(today, ZoneOffset.UTC));

        Instant upcomingBirthday = calculator.calculateUpcomingBirthday(28, 2, "Asia/Kolkata");

        Instant expected = LocalDateTime
                .of(2001, Month.FEBRUARY, 28, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        assertThat(upcomingBirthday).isEqualTo(expected);
    }

    @Test
    void calculateUpcomingBirthday_whenFebruary28BirthdayIsTodayInLeapYear() {
        Instant today = LocalDateTime
                .of(2000, Month.FEBRUARY, 28, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.fixed(today, ZoneOffset.UTC));

        Instant upcomingBirthday = calculator.calculateUpcomingBirthday(28, 2, "Asia/Kolkata");

        assertThat(upcomingBirthday).isEqualTo(today);
    }

    // calculateBirthdayForYear cases
    // no need to have a fixed clock here
    // cause birthdayForYear doesn't depend on a clock
    @Test
    void calculateBirthdayForYear_whenBirthdayIsNonLeap_andYearIsNonLeap() {

        Instant expectedBirthday = LocalDateTime
                .of(2001, Month.FEBRUARY, 28, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.systemUTC());

        Instant upcomingBirthday = calculator.calculateBirthdayForYear(28, 2, 2001, "Asia/Kolkata");

        assertThat(upcomingBirthday).isEqualTo(expectedBirthday);
    }

    @Test
    void calculateBirthdayForYear_whenBirthdayIsLeap_andYearIsLeap() {

        Instant expectedBirthday = LocalDateTime
                .of(2000, Month.FEBRUARY, 29, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.systemUTC());

        Instant upcomingBirthday = calculator.calculateBirthdayForYear(29, 2, 2000, "Asia/Kolkata");

        assertThat(upcomingBirthday).isEqualTo(expectedBirthday);
    }

    @Test
    void calculateBirthdayForYear_whenBirthdayIsLeap_andYearIsNonLeap() {

        Instant expectedBirthday = LocalDateTime
                .of(2001, Month.FEBRUARY, 28, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.systemUTC());

        Instant upcomingBirthday = calculator.calculateBirthdayForYear(29, 2, 2001, "Asia/Kolkata");

        assertThat(upcomingBirthday).isEqualTo(expectedBirthday);
    }

    @Test
    void calculateBirthdayForYear_whenBirthdayIsNonLeap_andYearIsLeap() {

        Instant expectedBirthday = LocalDateTime
                .of(2000, Month.FEBRUARY, 28, 0, 0, 0)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        calculator = new BirthdayScheduleCalculator(Clock.systemUTC());

        Instant upcomingBirthday = calculator.calculateBirthdayForYear(28, 2, 2000, "Asia/Kolkata");

        assertThat(upcomingBirthday).isEqualTo(expectedBirthday);
    }

}
