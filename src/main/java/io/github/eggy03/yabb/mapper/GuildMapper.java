package io.github.eggy03.yabb.mapper;

import io.github.eggy03.yabb.dto.GuildDTO;
import io.github.eggy03.yabb.entity.Guild;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class GuildMapper implements CommonMappingInterface<Guild, GuildDTO> {

    @Override
    public @NonNull Guild toEntity(@NonNull GuildDTO dto) {
        return new Guild(dto.getGuildId(), dto.getChannelId());
    }

    @Override
    public @NonNull List<Guild> toEntityList(@NonNull List<GuildDTO> dtoList) {
        if (dtoList.isEmpty()) return List.of();

        return dtoList
                .stream()
                .filter(Objects::nonNull)
                .map(this::toEntity)
                .toList();
    }

    @Override
    public @NonNull GuildDTO toDTO(@NonNull Guild entity) {
        return new GuildDTO(entity.getGuildId(), entity.getChannelId());
    }

    @Override
    public @NonNull List<GuildDTO> toDTOList(@NonNull List<Guild> entityList) {
        if (entityList.isEmpty()) return List.of();

        return entityList
                .stream()
                .filter(Objects::nonNull)
                .map(this::toDTO)
                .toList();
    }
}
