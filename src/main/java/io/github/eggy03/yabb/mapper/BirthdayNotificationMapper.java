package io.github.eggy03.yabb.mapper;

import io.github.eggy03.yabb.dto.BirthdayNotificationDTO;
import io.github.eggy03.yabb.entity.BirthdayNotification;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class BirthdayNotificationMapper {

    public @NonNull BirthdayNotificationDTO toDTO(@NonNull BirthdayNotification entity) {
        return new BirthdayNotificationDTO(entity.getGuildId(), entity.getUserId(), entity.getNextNotificationAt());
    }

    public @NonNull List<BirthdayNotificationDTO> toDTOList(@NonNull List<BirthdayNotification> entityList) {
        if (entityList.isEmpty()) return List.of();

        return entityList
                .stream()
                .filter(Objects::nonNull)
                .map(this::toDTO)
                .toList();
    }
}
