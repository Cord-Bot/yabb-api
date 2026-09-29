package io.github.eggy03.yabb.mapper;

import io.github.eggy03.yabb.dto.GuildDTO;
import io.github.eggy03.yabb.entity.Guild;
import jakarta.enterprise.context.ApplicationScoped;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class GuildMapper implements CommonMappingInterface<Guild, GuildDTO> {

    @Override
    public @Nullable Guild toEntity(@Nullable GuildDTO dto) {

        if (dto == null) return null;

        return new Guild(dto.getGuildId(), dto.getChannelId());
    }

    @Override
    public @NonNull List<Guild> toEntityList(@Nullable List<GuildDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty())
            return List.of();

        return dtoList
                .stream()
                .filter(Objects::nonNull)
                .map(this::toEntity)
                .toList();
    }

    @Override
    public @Nullable GuildDTO toDTO(@Nullable Guild entity) {

        if (entity == null)
            return null;

        return new GuildDTO(entity.getGuildId(), entity.getChannelId());
    }

    @Override
    public @NonNull List<GuildDTO> toDTOList(@Nullable List<Guild> entityList) {
        if (entityList == null || entityList.isEmpty())
            return List.of();

        return entityList
                .stream()
                .filter(Objects::nonNull)
                .map(this::toDTO)
                .toList();
    }
}
