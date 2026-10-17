package io.github.eggy03.yabb.utility;

import lombok.NonNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Refer to {@code src/test/docs/TEST_CASES_FOR_BIRTHDAY_CALCULATION.md}
 */
class BirthdayScheduleCalculatorTest {

    record TestBirthdayRecord(
            Instant today,
            int birthDay,
            int birthMonth,
            String zoneId,
            Instant expectedBirthday
    ) {}

    record TestBirthdayRecordForYear(
            int birthDay,
            int birthMonth,
            int forYear,
            String zoneId,
            Instant expectedBirthday
    ) {}

    static @NonNull Stream<TestBirthdayRecord> testBirthdayRecordStream() {
        ZoneId kolkata = ZoneId.of("Asia/Kolkata");

        return Stream.of(
                // Ordinary birthdays
                new TestBirthdayRecord(
                        LocalDate.of(2026, 10, 17).atStartOfDay(kolkata).toInstant(),
                        16, 10, kolkata.getId(),
                        LocalDate.of(2027, 10, 16).atStartOfDay(kolkata).toInstant()
                ),
                new TestBirthdayRecord(
                        LocalDate.of(2026, 10, 17).atStartOfDay(kolkata).toInstant(),
                        17, 10, kolkata.getId(),
                        LocalDate.of(2026, 10, 17).atStartOfDay(kolkata).toInstant()
                ),
                new TestBirthdayRecord(
                        LocalDate.of(2026, 10, 17).atStartOfDay(kolkata).toInstant(),
                        18, 10, kolkata.getId(),
                        LocalDate.of(2026, 10, 18).atStartOfDay(kolkata).toInstant()
                ),

                // Year boundaries
                new TestBirthdayRecord(
                        LocalDate.of(2026, 1, 1).atStartOfDay(kolkata).toInstant(),
                        31, 12, kolkata.getId(),
                        LocalDate.of(2026, 12, 31).atStartOfDay(kolkata).toInstant()
                ),
                new TestBirthdayRecord(
                        LocalDate.of(2026, 12, 31).atStartOfDay(kolkata).toInstant(),
                        1, 1, kolkata.getId(),
                        LocalDate.of(2027, 1, 1).atStartOfDay(kolkata).toInstant()
                ),

                // February 29 birthdays
                new TestBirthdayRecord(
                        LocalDate.of(2026, 2, 28).atStartOfDay(kolkata).toInstant(),
                        29, 2, kolkata.getId(),
                        LocalDate.of(2026, 2, 28).atStartOfDay(kolkata).toInstant()
                ),
                new TestBirthdayRecord(
                        LocalDate.of(2026, 3, 1).atStartOfDay(kolkata).toInstant(),
                        29, 2, kolkata.getId(),
                        LocalDate.of(2027, 2, 28).atStartOfDay(kolkata).toInstant()
                ),
                new TestBirthdayRecord(
                        LocalDate.of(2027, 2, 28).atStartOfDay(kolkata).toInstant(),
                        29, 2, kolkata.getId(),
                        LocalDate.of(2027, 2, 28).atStartOfDay(kolkata).toInstant()
                ),
                new TestBirthdayRecord(
                        LocalDate.of(2027, 3, 1).atStartOfDay(kolkata).toInstant(),
                        29, 2, kolkata.getId(),
                        LocalDate.of(2028, 2, 29).atStartOfDay(kolkata).toInstant()
                ),
                new TestBirthdayRecord(
                        LocalDate.of(2028, 2, 28).atStartOfDay(kolkata).toInstant(),
                        29, 2, kolkata.getId(),
                        LocalDate.of(2028, 2, 29).atStartOfDay(kolkata).toInstant()
                ),
                new TestBirthdayRecord(
                        LocalDate.of(2028, 2, 29).atStartOfDay(kolkata).toInstant(),
                        29, 2, kolkata.getId(),
                        LocalDate.of(2028, 2, 29).atStartOfDay(kolkata).toInstant()
                ),
                new TestBirthdayRecord(
                        LocalDate.of(2028, 2, 29).atStartOfDay(kolkata).toInstant(),
                        28, 2, kolkata.getId(),
                        LocalDate.of(2029, 2, 28).atStartOfDay(kolkata).toInstant()
                ),
                new TestBirthdayRecord(
                        LocalDate.of(2028, 3, 1).atStartOfDay(kolkata).toInstant(),
                        29, 2, kolkata.getId(),
                        LocalDate.of(2029, 2, 28).atStartOfDay(kolkata).toInstant()
                )
        );
    }

    static @NonNull Stream<TestBirthdayRecordForYear> testBirthdayRecordStreamForForYear() {
        ZoneId kolkata = ZoneId.of("Asia/Kolkata");

        return Stream.of(
                // non leap birthday, non leap year
                new TestBirthdayRecordForYear(
                        17, 10, 2001, kolkata.getId(),
                        LocalDate.of(2001, 10, 17).atStartOfDay(kolkata).toInstant()
                ),
                // non leap birthday, leap year
                new TestBirthdayRecordForYear(
                        17, 10, 2000, kolkata.getId(),
                        LocalDate.of(2000, 10, 17).atStartOfDay(kolkata).toInstant()
                ),
                // leap birthday, non leap year
                new TestBirthdayRecordForYear(
                        29, 2, 2001, kolkata.getId(),
                        LocalDate.of(2001, 2, 28).atStartOfDay(kolkata).toInstant()
                ),
                // leap birthday, leap year
                new TestBirthdayRecordForYear(
                        29, 2, 2000, kolkata.getId(),
                        LocalDate.of(2000, 2, 29).atStartOfDay(kolkata).toInstant()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testBirthdayRecordStream")
    void testCalculateBirthdayForYear(@NonNull TestBirthdayRecord data) {

        // fix a clock
        Clock clock = Clock.fixed(data.today, ZoneOffset.UTC);

        // prepare the calculator service
        BirthdayScheduleCalculator calculator = new BirthdayScheduleCalculator(clock);

        assertThat(data.expectedBirthday)
                .isEqualTo(calculator.calculateUpcomingBirthday(data.birthDay, data.birthMonth, data.zoneId));

    }


    @ParameterizedTest
    @MethodSource("testBirthdayRecordStreamForForYear")
    void testCalculateBirthdayForYear(@NonNull TestBirthdayRecordForYear data) {

        // no need to have a fixed clock here
        // cause calculateBirthdayForYear() doesn't depend on a clock
        BirthdayScheduleCalculator calculator = new BirthdayScheduleCalculator(Clock.systemUTC());

        assertThat(data.expectedBirthday)
                .isEqualTo(calculator.calculateBirthdayForYear(data.birthDay, data.birthMonth, data.forYear, data.zoneId));
    }

}
