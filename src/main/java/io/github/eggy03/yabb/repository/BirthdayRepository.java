package io.github.eggy03.yabb.repository;

import io.github.eggy03.yabb.entity.Birthday;
import io.github.eggy03.yabb.entity.BirthdayId;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

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
}
