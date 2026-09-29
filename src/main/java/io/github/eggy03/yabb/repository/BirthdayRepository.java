package io.github.eggy03.yabb.repository;

import io.github.eggy03.yabb.entity.Birthday;
import io.github.eggy03.yabb.entity.BirthdayId;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BirthdayRepository implements PanacheRepositoryBase<Birthday, BirthdayId> {
}
