package io.github.eggy03.yabb.utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.ZoneId;

@UtilityClass
public class BirthdayUtils {

    /**
     * Convenience method for {@link #nextBirthdayAt(MonthDay, ZoneId)}
     */
    @NonNull
    public static Instant nextBirthdayAt(int birthMonth, int birthDay, @NonNull String zoneIdString) {

        MonthDay birthMonthDay = MonthDay.of(birthMonth, birthDay);
        ZoneId zoneId = ZoneId.of(zoneIdString);

        return nextBirthdayAt(birthMonthDay, zoneId);
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
     * @param birthMonthDay the birth month and day
     * @param zoneId     the IANA time zone in which the birthday should be evaluated
     * @return the start of the next applicable birthday in the specified time zone,
     * represented as an {@link Instant}
     * @throws NullPointerException if {@code zoneId} is null
     */
    @NonNull
    public static Instant nextBirthdayAt(@NonNull MonthDay birthMonthDay, @NonNull ZoneId zoneId) {

        Year currentYear = Year.now(zoneId);

        LocalDate thisYearBirthDate = currentYear.atMonthDay(birthMonthDay);
        LocalDate nextYearBirthDate = currentYear.plusYears(1).atMonthDay(birthMonthDay);
        LocalDate today = LocalDate.now(zoneId);

        return today.isAfter(thisYearBirthDate) ?
                nextYearBirthDate.atStartOfDay(zoneId).toInstant() :
                thisYearBirthDate.atStartOfDay(zoneId).toInstant();
    }
}
