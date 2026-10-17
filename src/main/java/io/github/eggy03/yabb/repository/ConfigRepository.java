package io.github.eggy03.yabb.repository;

import io.github.eggy03.yabb.entity.Config;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConfigRepository implements PanacheRepositoryBase<Config, Long> {
}
