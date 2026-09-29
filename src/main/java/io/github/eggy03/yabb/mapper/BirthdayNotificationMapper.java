package io.github.eggy03.yabb.mapper;

import io.github.eggy03.yabb.dto.BirthdayNotificationDTO;
import io.github.eggy03.yabb.entity.BirthdayNotification;
import jakarta.enterprise.context.ApplicationScoped;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class BirthdayNotificationMapper {

    public @Nullable BirthdayNotificationDTO toDTO(@Nullable BirthdayNotification entity) {
        if (entity == null) return null;

        return new BirthdayNotificationDTO(entity.getGuildId(), entity.getUserId(), entity.getNextNotificationAt());
    }

    public @NonNull List<BirthdayNotificationDTO> toDTOList(@Nullable List<BirthdayNotification> entityList) {
        if (entityList == null || entityList.isEmpty())
            return List.of();

        return entityList
                .stream()
                .filter(Objects::nonNull)
                .map(this::toDTO)
                .toList();
    }
}
