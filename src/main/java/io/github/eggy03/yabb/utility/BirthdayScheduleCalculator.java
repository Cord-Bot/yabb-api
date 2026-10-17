package io.github.eggy03.yabb.utility;

import io.github.eggy03.yabb.annotation.UTCClock;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.ZoneId;

@ApplicationScoped
public class BirthdayScheduleCalculator {

    // clock produced by our custom bean
    private final @NonNull Clock clock;

    public BirthdayScheduleCalculator(@NonNull @UTCClock Clock clock) {
        this.clock = clock;
    }

    /**
     * Calculates the next instant for a given birth month and day.
     *
     * <p>If the birth month and day has already passed in the current year (in the user's time-zone),
     * the upcoming birthday is set for the next year.
     * If the birthday is today or in a future date, the upcoming birthday is set for the current year.</p>
     *
     * <p>If the birthday is on February 29
     * and the year in which the user will be wished is not a leap year, the
     * birthday is treated as February 28. For a leap year, it remains February 29.</p>
     *
     * <p>The time of day is always set to (00:00), depending on the user's time-zone.</p>
     *
     * <p>NOTE: This method always uses the present year for the given {@code ZoneId}.
     * All calculations are based on the present year.</p>
     *
     * <h3>Examples</h3>
     *
     * <table border="1">
     *   <tr>
     *     <th>Today</th>
     *     <th>Birthday</th>
     *     <th>Zone ID</th>
     *     <th>Upcoming Birthday (Local Date Time)</th>
     *     <th>Upcoming Birthday (Instant)</th>
     *   </tr>
     *   <tr>
     *     <td>2026-09-30</td>
     *     <td>09-30</td>
     *     <td>{@code America/New_York}</td>
     *     <td>2026-09-30T00:00:00-04:00</td>
     *     <td>2026-09-30T04:00:00Z</td>
     *   </tr>
     *   <tr>
     *     <td>2026-09-30</td>
     *     <td>10-15</td>
     *     <td>{@code America/New_York}</td>
     *     <td>2026-10-15T00:00:00-04:00</td>
     *     <td>2026-10-15T04:00:00Z</td>
     *   </tr>
     *   <tr>
     *     <td>2026-09-30</td>
     *     <td>09-29</td>
     *     <td>{@code America/New_York}</td>
     *     <td>2027-09-29T00:00:00-04:00</td>
     *     <td>2027-09-29T04:00:00Z</td>
     *   </tr>
     *   <tr>
     *     <td>2026-09-30</td>
     *     <td>02-29</td>
     *     <td>{@code America/New_York}</td>
     *     <td>2027-02-28T00:00:00-05:00</td>
     *     <td>2027-02-28T05:00:00Z</td>
     *   </tr>
     *   <tr>
     *     <td>2027-09-30</td>
     *     <td>02-29</td>
     *     <td>{@code America/New_York}</td>
     *     <td>2028-02-29T00:00:00-05:00</td>
     *     <td>2028-02-29T05:00:00Z</td>
     *   </tr>
     * </table>
     *
     * @param birthDay     the birthday
     * @param birthMonth   the birth month
     * @param zoneIdString the IANA time zone in which the birthday should be evaluated
     * @return the start of the next applicable birthday in the specified time zone,
     * represented as an {@link Instant}
     * @throws NullPointerException if {@code zoneIdString} is null
     */
    @NonNull
    public Instant calculateUpcomingBirthday(int birthDay, int birthMonth, @NonNull String zoneIdString) {

        MonthDay birthMonthDay = MonthDay.of(birthMonth, birthDay);
        ZoneId zoneId = ZoneId.of(zoneIdString);

        // capture today only ONCE and derive currentYear from it
        // if u use Year.now() while u have LocalDate.now()
        // it may straddle at end of the year boundaries
        LocalDate today = LocalDate.now(clock.withZone(zoneId));
        Year currentYear = Year.from(today);

        LocalDate thisYearBirthDate = currentYear.atMonthDay(birthMonthDay);
        LocalDate nextYearBirthDate = currentYear.plusYears(1).atMonthDay(birthMonthDay);

        return today.isAfter(thisYearBirthDate)
                ? nextYearBirthDate.atStartOfDay(zoneId).toInstant()
                : thisYearBirthDate.atStartOfDay(zoneId).toInstant();
    }

    /**
     * Calculates the birthday for the provided year.
     *
     * <p>The birthday is evaluated at {@code 00:00} in the provided IANA
     * time zone and converted to an {@link Instant}.</p>
     *
     * <p>Leap-day birthdays ({@code February 29}) are treated as
     * {@code February 28} when the year is not a leap year.</p>
     *
     * @param birthDay     the birthday
     * @param birthMonth   the birth month
     * @param year  the year on which this birthday has occurred or was supposed to occur
     * @param zoneIdString the IANA time zone in which the birthday is evaluated
     * @return the birthday for the given year, as an
     * {@link Instant}
     * @throws NullPointerException if {@code zoneIdString} is null
     */
    @NonNull
    public Instant calculateBirthdayForYear(int birthDay, int birthMonth, int year, @NonNull String zoneIdString) {

        return MonthDay.of(birthMonth, birthDay)
                .atYear(year)
                .atStartOfDay(ZoneId.of(zoneIdString))
                .toInstant();
    }
}