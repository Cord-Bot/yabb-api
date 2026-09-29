package io.github.eggy03.yabb.repository;

import io.github.eggy03.yabb.entity.BirthdayId;
import io.github.eggy03.yabb.entity.BirthdayNotification;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class BirthdayNotificationRepository implements PanacheRepositoryBase<BirthdayNotification, BirthdayId> {

    public List<BirthdayNotification> findPendingNotifications() {
        return find("nextNotificationAt <= :now", Map.of("now", Instant.now())).list();
    }
}
