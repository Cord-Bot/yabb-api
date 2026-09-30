package io.github.eggy03.yabb.service;

import io.github.eggy03.yabb.dto.BirthdayRequestDTO;
import io.github.eggy03.yabb.dto.BirthdayResponseDTO;
import io.github.eggy03.yabb.entity.Birthday;
import io.github.eggy03.yabb.entity.BirthdayId;
import io.github.eggy03.yabb.exception.BirthdayNotFoundException;
import io.github.eggy03.yabb.mapper.BirthdayRequestMapper;
import io.github.eggy03.yabb.mapper.BirthdayResponseMapper;
import io.github.eggy03.yabb.repository.BirthdayRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
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
        entity.setNextNotificationAt(calculateNextNotificationAt(dto.getMonth(), dto.getDay(), dto.getZoneId()));

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
        entity.setNextNotificationAt(calculateNextNotificationAt(updatedDTO.getMonth(), updatedDTO.getDay(), updatedDTO.getZoneId()));
    }

    @Transactional
    public void updateBirthdayForNextYear(long guildId, long userId) {

        Birthday entity = birthdayRepository
                .findByIdOptional(new BirthdayId(guildId, userId))
                .orElseThrow(()-> new BirthdayNotFoundException("No entry found for user: %s in guild: %s".formatted(guildId, userId)));

        ZoneId zoneId = ZoneId.of(entity.getZoneId());
        Year currentNotificationYear = Year.from(entity.getNextNotificationAt().atZone(zoneId));

        // dirty checking
        entity.setNextNotificationAt(
                birthDateForYear(entity.getMonth(), entity.getDay(), currentNotificationYear.plusYears(1))
                        .atStartOfDay(zoneId)
                        .toInstant()
        );
    }

    @Transactional
    public void deleteBirthday(long guildId, long userId) {

        if (!birthdayRepository.deleteById(new BirthdayId(guildId, userId)))
            throw new BirthdayNotFoundException("No entry found for user: %s in guild: %s".formatted(guildId, userId)) ;
    }

    // for the given zoneId
    // if the birthdate has already passed for the current year, then set the notification to occur next year
    // else set it for this year's birthdate
    @NonNull
    Instant calculateNextNotificationAt(int birthMonth, int birthDay, @NonNull String zoneIdString) {

        // don't use time to compare cause
        // we want the notification to work as long as the birthdate day isn't over
        // this way, a user who enters their birthdate for the first time, on the day
        // of their birthday, will still be notified

        ZoneId zoneId = ZoneId.of(zoneIdString);

        LocalDate thisYearBirthDate = birthDateForYear(birthMonth, birthDay, Year.now(zoneId));
        LocalDate nextYearBirthDate = birthDateForYear(birthMonth, birthDay, Year.now(zoneId).plusYears(1));
        LocalDate today = LocalDate.now(zoneId);

        return today.isAfter(thisYearBirthDate) ?
                nextYearBirthDate.atStartOfDay(zoneId).toInstant() :
                thisYearBirthDate.atStartOfDay(zoneId).toInstant();
    }

    LocalDate birthDateForYear(int birthMonth, int birthDay, @NonNull Year year) {

        if (!year.isLeap() && birthMonth == 2 && birthDay == 29)
            return LocalDate.of(year.getValue(), birthMonth, birthDay - 1);

        return LocalDate.of(
                year.getValue(),
                birthMonth,
                birthDay
        );
    }
}
