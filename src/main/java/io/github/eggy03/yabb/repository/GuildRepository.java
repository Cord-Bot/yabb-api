package io.github.eggy03.yabb.repository;

import io.github.eggy03.yabb.entity.Guild;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GuildRepository implements PanacheRepositoryBase<Guild, Long> {
}
