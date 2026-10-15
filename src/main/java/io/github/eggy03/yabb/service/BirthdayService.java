package io.github.eggy03.yabb.service;

import io.github.eggy03.yabb.dto.BirthdayDTO;
import io.github.eggy03.yabb.entity.Birthday;
import io.github.eggy03.yabb.entity.BirthdayId;
import io.github.eggy03.yabb.entity.BirthdaySchedule;
import io.github.eggy03.yabb.exception.BirthdayNotFoundException;
import io.github.eggy03.yabb.exception.BirthdayScheduleNotFoundException;
import io.github.eggy03.yabb.mapper.BirthdayMapper;
import io.github.eggy03.yabb.repository.BirthdayRepository;
import io.github.eggy03.yabb.repository.BirthdayScheduleRepository;
import io.github.eggy03.yabb.utility.BirthdayScheduleUtils;
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
    private final @NonNull BirthdayScheduleRepository birthdayScheduleRepository;

    @Transactional
    public void saveBirthday(@NonNull BirthdayDTO dto) {

        // create birthday entity
        Birthday birthday = birthdayMapper.toEntity(dto);

        // persist birthday
        birthdayRepository.persist(birthday);

        // create schedule entity
        BirthdaySchedule birthdaySchedule = new BirthdaySchedule(dto.getGuildId(), dto.getUserId(), null);
        birthdaySchedule.setUpcomingBirthdayAt(BirthdayScheduleUtils.calculateUpcomingBirthday(dto.getDay(), dto.getMonth(), dto.getZoneId()));

        // persist schedule
        birthdayScheduleRepository.persist(birthdaySchedule);

    }

    @Transactional
    public BirthdayDTO getBirthday(long guildId, long userId) {
        Birthday entity =  birthdayRepository
                .findByIdOptional(new BirthdayId(guildId, userId))
                .orElseThrow(()-> new BirthdayNotFoundException("No birthday found for user: [%s] in guild: [%s]".formatted(userId, guildId)));

        return birthdayMapper.toDTO(entity);
    }

    @Transactional
    public List<BirthdayDTO> getBirthdays(long guildId) {
        return birthdayMapper.toDTOList(birthdayRepository.findByGuildId(guildId));
    }

    @Transactional
    public void updateBirthday(@NonNull BirthdayDTO updatedDTO) {

        // find birthday
        Birthday birthday =  birthdayRepository
                .findByIdOptional(new BirthdayId(updatedDTO.getGuildId(), updatedDTO.getUserId()))
                .orElseThrow(()-> new BirthdayNotFoundException("No birthday found for user: [%s] in guild: [%s]".formatted(updatedDTO.getUserId(), updatedDTO.getGuildId())));

        // update birthday by dirty checking
        birthday.setMonth(updatedDTO.getMonth());
        birthday.setDay(updatedDTO.getDay());
        birthday.setZoneId(updatedDTO.getZoneId());

        // find schedule
        BirthdaySchedule birthdaySchedule = birthdayScheduleRepository
                .findByIdOptional(new BirthdayId(updatedDTO.getGuildId(), updatedDTO.getUserId()))
                .orElseThrow(()-> new BirthdayScheduleNotFoundException("No schedule found for user: [%s] in guild: [%s]".formatted(updatedDTO.getUserId(), updatedDTO.getGuildId())));

        // update schedule by dirty checking
        birthdaySchedule.setUpcomingBirthdayAt(
                BirthdayScheduleUtils.calculateUpcomingBirthday(updatedDTO.getDay(), updatedDTO.getMonth(), updatedDTO.getZoneId())
        );
    }

    @Transactional
    public void deleteBirthday(long guildId, long userId) {

        if (!birthdayRepository.deleteById(new BirthdayId(guildId, userId)))
            throw new BirthdayNotFoundException("No birthday found for user: [%s] in guild: [%s]".formatted(userId, guildId));

        // no need to delete schedule separately
        // db will delete them automatically if birthday is deleted
    }

    @Transactional
    public long deleteBirthdayByGuild(long guildId) {
        return birthdayRepository.deleteByGuildId(guildId);
    }
}
