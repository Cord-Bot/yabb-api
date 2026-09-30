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
     * Calculates the next instant for a birthday wish.
     *
     * <p>The time of day is always set to the start of the birthday (00:00),
     * and the resulting instant is calculated using the IANA {@code zoneId}
     * of the birthday user.</p>
     *
     * <p>Firstly, if the birthday is on February 29
     * and the year in which the user will be wished is not a leap year, the
     * birthday is treated as February 28. For a leap year, it remains February 29.</p>
     *
     * <p>If the birthday has already passed in the current year, the wish is
     * scheduled for the same birthday in the following year. If the birthday
     * is today or is still upcoming, the wish is scheduled for the current year.</p>
     *
     * <h3>Examples</h3>
     *
     * <table border="1">
     *   <tr>
     *     <th>Today</th>
     *     <th>Birthday (Month-Day)</th>
     *     <th>Result (Local Time)</th>
     *   </tr>
     *   <tr>
     *     <td>2026-09-30</td>
     *     <td>09-30</td>
     *     <td>2026-09-30T00:00:00</td>
     *   </tr>
     *   <tr>
     *     <td>2026-09-30</td>
     *     <td>10-15</td>
     *     <td>2026-10-15T00:00:00</td>
     *   </tr>
     *   <tr>
     *     <td>2026-09-30</td>
     *     <td>09-29</td>
     *     <td>2027-09-29T00:00:00</td>
     *   </tr>
     *   <tr>
     *     <td>2026-09-30</td>
     *     <td>02-29</td>
     *     <td>2027-02-28T00:00:00</td>
     *   </tr>
     *   <tr>
     *     <td>2027-09-30</td>
     *     <td>02-29</td>
     *     <td>2028-02-29T00:00:00</td>
     *   </tr>
     * </table>
     *
     * @param monthDay the birthday month and day
     * @param zoneId   the IANA time zone in which the birthday should be evaluated
     * @return the start of the next applicable birthday in the specified time zone,
     *         represented as an {@link Instant}
     * @throws NullPointerException if {@code monthDay} or {@code zoneId} is null
     */
    @NonNull
    public static Instant nextWishAt(@NonNull MonthDay monthDay, @NonNull ZoneId zoneId) {

        LocalDate thisYearBirthDate = Year.now(zoneId).atMonthDay(monthDay);
        LocalDate nextYearBirthDate = Year.now(zoneId).plusYears(1).atMonthDay(monthDay);
        LocalDate today = LocalDate.now(zoneId);

        return today.isAfter(thisYearBirthDate) ?
                nextYearBirthDate.atStartOfDay(zoneId).toInstant() :
                thisYearBirthDate.atStartOfDay(zoneId).toInstant();
    }
}
