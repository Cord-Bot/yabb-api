package io.github.eggy03.yabb.service;

import io.github.eggy03.yabb.dto.GuildDTO;
import io.github.eggy03.yabb.entity.Guild;
import io.github.eggy03.yabb.exception.GuildNotFoundException;
import io.github.eggy03.yabb.mapper.GuildMapper;
import io.github.eggy03.yabb.repository.GuildRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class GuildService {

    private final @NonNull GuildMapper guildMapper;
    private final @NonNull GuildRepository guildRepository;

    @Transactional
    public void saveGuild(@NonNull GuildDTO dto) {
        guildRepository.persist(guildMapper.toEntity(dto));
    }

    @Transactional
    public @NonNull GuildDTO getGuild(long guildId) {

        Guild entity =  guildRepository
                .findByIdOptional(guildId)
                .orElseThrow(()-> new GuildNotFoundException("Guild with ID: %s not found".formatted(guildId)));

        return guildMapper.toDTO(entity);
    }

    @Transactional
    public @NonNull List<GuildDTO> getAllGuilds() {
        return guildMapper.toDTOList(guildRepository.listAll());
    }

    @Transactional
    public void updateChannelId(long guildId, long newChannelId) {

        Guild existingEntity = guildRepository
                .findByIdOptional(guildId)
                .orElseThrow(()-> new GuildNotFoundException("Update Failed: Guild with ID: %s not found".formatted(guildId)));

        // dirty checking
        existingEntity.setChannelId(newChannelId);
    }

    @Transactional
    public void deleteGuild(long guildId) {
        if (!guildRepository.deleteById(guildId))
            throw new GuildNotFoundException("Deletion Failed: Guild with ID: %s not found".formatted(guildId));

    }

}
