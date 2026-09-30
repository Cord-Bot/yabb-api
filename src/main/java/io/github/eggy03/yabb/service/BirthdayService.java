package io.github.eggy03.yabb.service;

import io.github.eggy03.yabb.dto.BirthdayRequestDTO;
import io.github.eggy03.yabb.dto.BirthdayResponseDTO;
import io.github.eggy03.yabb.entity.Birthday;
import io.github.eggy03.yabb.entity.BirthdayId;
import io.github.eggy03.yabb.exception.BirthdayNotFoundException;
import io.github.eggy03.yabb.mapper.BirthdayRequestMapper;
import io.github.eggy03.yabb.mapper.BirthdayResponseMapper;
import io.github.eggy03.yabb.repository.BirthdayRepository;
import io.github.eggy03.yabb.utility.BirthdayUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.MonthDay;
import java.time.Year;
import java.time.ZoneId;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@SuppressWarnings("java:S1192")
public class BirthdayService {

    private final @NonNull BirthdayRequestMapper birthdayRequestMapper;
    private final @NonNull BirthdayResponseMapper birthdayResponseMapper;
    private final @NonNull BirthdayRepository birthdayRepository;

    @Transactional
    public void saveBirthday(@NonNull BirthdayRequestDTO dto) {

        Birthday entity = birthdayRequestMapper.toEntity(dto);
        // calculate the next notification time
        MonthDay monthDay = MonthDay.of(dto.getMonth(), dto.getDay());
        ZoneId zoneId = ZoneId.of(dto.getZoneId());
        entity.setNextNotificationAt(BirthdayUtils.nextWishAt(monthDay, zoneId));

        birthdayRepository.persist(entity);
    }

    @Transactional
    public BirthdayResponseDTO getBirthday(long guildId, long userId) {
        Birthday entity =  birthdayRepository
                .findByIdOptional(new BirthdayId(guildId, userId))
                .orElseThrow(()-> new BirthdayNotFoundException("No entry found for user: %s in guild: %s".formatted(userId, guildId)));

        return birthdayResponseMapper.toDTO(entity);
    }

    @Transactional
    public List<BirthdayResponseDTO> getBirthdays(long guildId) {
        return birthdayResponseMapper.toDTOList(birthdayRepository.findByGuildId(guildId));
    }

    @Transactional
    public @NonNull List<BirthdayResponseDTO> getPendingBirthdays() {
        return birthdayResponseMapper.toDTOList(birthdayRepository.findPendingBirthdays());
    }

    @Transactional
    public void updateBirthday(@NonNull BirthdayRequestDTO updatedDTO) {

        Birthday entity =  birthdayRepository
                .findByIdOptional(new BirthdayId(updatedDTO.getGuildId(), updatedDTO.getUserId()))
                .orElseThrow(()-> new BirthdayNotFoundException("No entry found for user: %s in guild: %s".formatted(updatedDTO.getGuildId(), updatedDTO.getUserId())));

        // dirty check
        entity.setMonth(updatedDTO.getMonth());
        entity.setDay(updatedDTO.getDay());
        entity.setZoneId(updatedDTO.getZoneId());

        // re-calculate the next notification time
        MonthDay monthDay = MonthDay.of(updatedDTO.getMonth(), updatedDTO.getDay());
        ZoneId zoneId = ZoneId.of(updatedDTO.getZoneId());
        entity.setNextNotificationAt(BirthdayUtils.nextWishAt(monthDay, zoneId));
    }

    @Transactional
    public void updateBirthdayForNextYear(long guildId, long userId) {

        Birthday entity = birthdayRepository
                .findByIdOptional(new BirthdayId(guildId, userId))
                .orElseThrow(()-> new BirthdayNotFoundException("No entry found for user: %s in guild: %s".formatted(guildId, userId)));

        ZoneId zoneId = ZoneId.of(entity.getZoneId());
        MonthDay birthMonthDay = MonthDay.of(entity.getMonth(), entity.getDay());
        Year currentNotificationYear = Year.from(entity.getNextNotificationAt().atZone(zoneId));

        // dirty checking
        entity.setNextNotificationAt(
                currentNotificationYear.plusYears(1).atMonthDay(birthMonthDay)
                        .atStartOfDay(zoneId)
                        .toInstant()
        );
    }

    @Transactional
    public void deleteBirthday(long guildId, long userId) {

        if (!birthdayRepository.deleteById(new BirthdayId(guildId, userId)))
            throw new BirthdayNotFoundException("No entry found for user: %s in guild: %s".formatted(guildId, userId)) ;
    }
}
