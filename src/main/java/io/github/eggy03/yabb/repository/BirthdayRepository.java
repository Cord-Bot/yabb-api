package io.github.eggy03.yabb.repository;

import io.github.eggy03.yabb.entity.Birthday;
import io.github.eggy03.yabb.entity.BirthdayId;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class BirthdayRepository implements PanacheRepositoryBase<Birthday, BirthdayId> {

    public List<Birthday> findByGuildId(long guildId) {
        return find("guildId = :value", Map.of("value", guildId)).list();
    }

    public long deleteByGuildId(long guildId) {
        return delete("guildId = :value", Map.of("value", guildId));
    }


    public List<Birthday> findUpcomingBirthdays() {
        return find("upcomingBirthday > :now", Map.of("now", Instant.now())).list();
    }

    public List<Birthday> findActiveBirthdays() {
        Instant now = Instant.now();
        Instant cutoff = now.minus(24, ChronoUnit.HOURS);

        return find("upcomingBirthday <= :now AND upcomingBirthday >= :cutoff",
                Map.of("now", now, "cutoff", cutoff)
        ).list();
    }

    public List<Birthday> findExpiredBirthdays() {
        Instant cutoff = Instant.now().minus(24, ChronoUnit.HOURS);
        return find("upcomingBirthday < :cutoff", Map.of("cutoff", cutoff)).list();
    }
}
