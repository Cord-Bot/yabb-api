package io.github.eggy03.yabb.repository;

import io.github.eggy03.yabb.entity.BirthdayId;
import io.github.eggy03.yabb.entity.BirthdayNotification;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BirthdayNotificationRepository implements PanacheRepositoryBase<BirthdayNotification, BirthdayId> {
}
