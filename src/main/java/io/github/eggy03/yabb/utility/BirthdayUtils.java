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
    public static Instant nextBirthdayAt(int birthDay, int birthMonth, @NonNull String zoneIdString) {

        MonthDay birthMonthDay = MonthDay.of(birthMonth, birthDay);
        ZoneId zoneId = ZoneId.of(zoneIdString);

        Year currentYear = Year.now(zoneId);

        LocalDate thisYearBirthDate = currentYear.atMonthDay(birthMonthDay);
        LocalDate nextYearBirthDate = currentYear.plusYears(1).atMonthDay(birthMonthDay);
        LocalDate today = LocalDate.now(zoneId);

        return today.isAfter(thisYearBirthDate) ?
                nextYearBirthDate.atStartOfDay(zoneId).toInstant() :
                thisYearBirthDate.atStartOfDay(zoneId).toInstant();
    }

    /**
     * Adds a year to the provided birthday.
     *
     * <p>The birthday is evaluated at {@code 00:00} in the provided IANA
     * time zone and converted to an {@link Instant}.</p>
     *
     * <p>Leap-day birthdays ({@code February 29}) are treated as
     * {@code February 28} when the following year is not a leap year.</p>
     *
     * @param birthDay     the birthday
     * @param birthMonth   the birth month
     * @param currentYear  the year on which this birthday has occurred or was supposed to occur
     * @param zoneIdString the IANA time zone in which the birthday is evaluated
     * @return the birthday time after adding a year, as an
     * {@link Instant}
     * @throws NullPointerException if {@code zoneIdString} is null
     */
    @NonNull
    public static Instant nextYearBirthdayAt(int birthDay, int birthMonth, int currentYear, @NonNull String zoneIdString) {

        // previously this used to use Year.now()
        // that is it could always take the current year and add 1 to it

        // however this would create the following issue
        // Suppose a birthday which was supposed to occur on 31 December 2026
        // did not occur because the bot was down
        // Now, on January 2, 2027, the API calls this function on that expired birthday
        // to silently update for December 31, 2027

        // but since Year.now() was used for calculation and was called on January 2, 2027,
        // it would set the next birthday to December 31, 2028, instead
        // the newest change adds 1 year based on the currentYear received

        // do not use LocalDate.of(currentYear, birthMonth, birthDay)
        // because currentYear, birthMonth, birthDay are not tightly related and may be invalid for a given year
        // this is because birthDay and birthMonth represents a user's birthday and month
        // while currentYear represents the last known year on which a person was wished
        // it may or may not have been a leap year
        // so if someone's birthday and month is February 29 but the current year received is 2025
        // LocalDate will throw error
        // MonthDay.atYear will convert February 29 to 28
        return MonthDay.of(birthMonth, birthDay)
                .atYear(currentYear)
                .plusYears(1)
                .atStartOfDay(ZoneId.of(zoneIdString))
                .toInstant();

        // future reference: the reason why currentYear, birthMonth, birthDay was provided
        // instead of the direct Instant at which the last birthday had occurred
        // is because non leap year instants would be converted to 28 Feb and adding +1 to it would've always
        // kept it 28
        // we want leap year birthdays to happen in 29 itself, so by freshly providing birthMonth, birthDay
        // we preserve the actual birthday and month of a user
    }
}