package io.github.eggy03.yabb.repository;

import io.github.eggy03.yabb.annotation.UTCClock;
import io.github.eggy03.yabb.entity.BirthdayId;
import io.github.eggy03.yabb.entity.BirthdaySchedule;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class BirthdayScheduleRepository implements PanacheRepositoryBase<BirthdaySchedule, BirthdayId> {

    // clock produced by our custom bean
    private final @NonNull Clock clock;

    public BirthdayScheduleRepository(@NonNull @UTCClock Clock clock) {
        this.clock = clock;
    }

    public List<BirthdaySchedule> findUpcomingSchedules() {
        return find("upcomingBirthdayAt > :now", Map.of("now", clock.instant())).list();
    }

    public List<BirthdaySchedule> findActiveSchedules() {
        Instant now = clock.instant();
        Instant cutoff = now.minus(24, ChronoUnit.HOURS);

        return find("upcomingBirthdayAt <= :now AND upcomingBirthdayAt >= :cutoff",
                Map.of("now", now, "cutoff", cutoff)
        ).list();
    }

    public List<BirthdaySchedule> findExpiredSchedules() {
        Instant cutoff = clock.instant().minus(24, ChronoUnit.HOURS);
        return find("upcomingBirthdayAt < :cutoff", Map.of("cutoff", cutoff)).list();
    }
}
