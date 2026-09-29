package io.github.eggy03.yabb.service;

import io.github.eggy03.yabb.dto.BirthdayDTO;
import io.github.eggy03.yabb.entity.Birthday;
import io.github.eggy03.yabb.entity.BirthdayId;
import io.github.eggy03.yabb.exception.BirthdayNotFoundException;
import io.github.eggy03.yabb.mapper.BirthdayMapper;
import io.github.eggy03.yabb.repository.BirthdayRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@SuppressWarnings("java:S1192")
public class BirthdayService {

    private final @NonNull BirthdayMapper birthdayMapper;
    private final @NonNull BirthdayRepository birthdayRepository;
    private final @NonNull BirthdayNotificationService birthdayNotificationService;

    @Transactional
    public void saveBirthday(@NonNull BirthdayDTO dto) {
        birthdayRepository.persist(birthdayMapper.toEntity(dto));

        // create notification
        birthdayNotificationService.createNotification(dto.getGuildId(), dto.getUserId(), dto.getBirthMonth(), dto.getBirthDay(), dto.getTimeZone());
    }

    @Transactional
    public BirthdayDTO getBirthday(long guildId, long userId) {
        Birthday entity =  birthdayRepository
                .findByIdOptional(new BirthdayId(guildId, userId))
                .orElseThrow(()-> new BirthdayNotFoundException("No entry found for user: %s in guild: %s".formatted(userId, guildId)));

        return birthdayMapper.toDTO(entity);
    }

    @Transactional
    public List<BirthdayDTO> getBirthdays(long guildId) {
        return birthdayMapper.toDTOList(birthdayRepository.findByGuildId(guildId));
    }

    @Transactional
    public void updateBirthday(@NonNull BirthdayDTO updatedDTO) {

        Birthday entity =  birthdayRepository
                .findByIdOptional(new BirthdayId(updatedDTO.getGuildId(), updatedDTO.getUserId()))
                .orElseThrow(()-> new BirthdayNotFoundException("No entry found for user: %s in guild: %s".formatted(updatedDTO.getGuildId(), updatedDTO.getUserId())));

        // dirty check
        entity.setBirthMonth(updatedDTO.getBirthMonth());
        entity.setBirthDay(updatedDTO.getBirthDay());
        entity.setTimeZone(updatedDTO.getTimeZone());

        // update notification
        birthdayNotificationService.updateNotification(
                updatedDTO.getGuildId(), updatedDTO.getUserId(),
                updatedDTO.getBirthMonth(), updatedDTO.getBirthDay(), updatedDTO.getTimeZone()
        );
    }

    @Transactional
    public void deleteBirthday(long guildId, long userId) {

        if (!birthdayRepository.deleteById(new BirthdayId(guildId, userId)))
            throw new BirthdayNotFoundException("No entry found for user: %s in guild: %s".formatted(guildId, userId)) ;

        // also delete notification
        birthdayNotificationService.deleteNotification(guildId, userId);
    }
}
