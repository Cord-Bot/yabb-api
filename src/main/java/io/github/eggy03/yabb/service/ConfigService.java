package io.github.eggy03.yabb.service;

import io.github.eggy03.yabb.dto.ConfigDTO;
import io.github.eggy03.yabb.entity.Config;
import io.github.eggy03.yabb.exception.ConfigNotFoundException;
import io.github.eggy03.yabb.mapper.ConfigMapper;
import io.github.eggy03.yabb.repository.ConfigRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class ConfigService {

    private final @NonNull ConfigMapper configMapper;
    private final @NonNull ConfigRepository configRepository;

    @Transactional
    public void saveConfig(@NonNull ConfigDTO dto) {
        configRepository.persist(configMapper.toEntity(dto));
    }

    @Transactional
    public @NonNull ConfigDTO getConfig(long guildId) {

        Config entity =  configRepository
                .findByIdOptional(guildId)
                .orElseThrow(()-> new ConfigNotFoundException("Config with GuildID: [%s] not found".formatted(guildId)));

        return configMapper.toDTO(entity);
    }

    @Transactional
    public @NonNull List<ConfigDTO> getAllConfigs() {
        return configMapper.toDTOList(configRepository.listAll());
    }

    @Transactional
    public void updateConfig(@NonNull ConfigDTO dto) {

        Config existingEntity = configRepository
                .findByIdOptional(dto.getGuildId())
                .orElseThrow(()-> new ConfigNotFoundException("Update Failed: Config with GuildID: [%s] not found".formatted(dto.getGuildId())));

        // dirty checking
        existingEntity.setChannelId(dto.getChannelId());
        existingEntity.setRoleId(dto.getRoleId());
    }

    @Transactional
    public void deleteConfig(long guildId) {
        if (!configRepository.deleteById(guildId))
            throw new ConfigNotFoundException("Deletion Failed: Config with GuildID: [%s] not found".formatted(guildId));

    }

}
