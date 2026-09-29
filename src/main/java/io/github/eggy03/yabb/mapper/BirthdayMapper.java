package io.github.eggy03.yabb.mapper;

import io.github.eggy03.yabb.dto.BirthdayDTO;
import io.github.eggy03.yabb.entity.Birthday;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;

import java.time.MonthDay;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class BirthdayMapper implements CommonMappingInterface<Birthday, BirthdayDTO> {

    @Override
    public @NonNull Birthday toEntity(@NonNull BirthdayDTO dto) {
        
        // will validate the date and month received and throw exceptions for bad combinations
        MonthDay monthDay = MonthDay.of(dto.getBirthMonth(), dto.getBirthDay());

        // will validate the time-zone ID and throw exceptions for invalid formats
        ZoneId zoneId = ZoneId.of(dto.getTimeZone());

        return new Birthday(
                dto.getGuildId(),
                dto.getUserId(),
                monthDay.getMonthValue(),
                monthDay.getDayOfMonth(),
                zoneId.getId()
        );
    }

    @Override
    public @NonNull List<Birthday> toEntityList(@NonNull List<BirthdayDTO> dtoList) {
        if (dtoList.isEmpty()) return List.of();

        return dtoList
                .stream()
                .filter(Objects::nonNull)
                .map(this::toEntity)
                .toList();
    }

    @Override
    public @NonNull BirthdayDTO toDTO(@NonNull Birthday entity) {

        // no need to validate month-day and time-zone id
        // we assume that an entity, before being stored is converted from dto to entity by this mapper
        // which already has validation enabled. This means no malformed month-day will enter into the db
        // under natural circumstances
        // moreover, we will be redirecting all DateTimeExceptions to HTTP400

        return new BirthdayDTO(
                entity.getGuildId(),
                entity.getUserId(),
                entity.getBirthMonth(),
                entity.getBirthDay(),
                entity.getTimeZone()
        );
    }

    @Override
    public @NonNull List<BirthdayDTO> toDTOList(@NonNull List<Birthday> entityList) {
        if (entityList.isEmpty()) return List.of();

        return entityList
                .stream()
                .filter(Objects::nonNull)
                .map(this::toDTO)
                .toList();
    }
}
