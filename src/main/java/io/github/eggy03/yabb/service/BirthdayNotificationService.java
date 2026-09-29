package io.github.eggy03.yabb.service;

import io.github.eggy03.yabb.dto.BirthdayNotificationDTO;
import io.github.eggy03.yabb.entity.BirthdayId;
import io.github.eggy03.yabb.entity.BirthdayNotification;
import io.github.eggy03.yabb.exception.BirthdayNotificationNotFoundException;
import io.github.eggy03.yabb.mapper.BirthdayNotificationMapper;
import io.github.eggy03.yabb.repository.BirthdayNotificationRepository;
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
public class BirthdayNotificationService {

    private final @NonNull BirthdayNotificationMapper birthdayNotificationMapper;
    private final @NonNull BirthdayNotificationRepository birthdayNotificationRepository;

    @Transactional
    public void createNotification(long guildId, long userId, int birthMonth, int birthDate, @NonNull String zoneIdString) {

        BirthdayNotification entity = new BirthdayNotification();

        entity.setGuildId(guildId);
        entity.setUserId(userId);
        entity.setNextNotificationAt(calculateNextNotificationAt(birthMonth, birthDate, zoneIdString));

        birthdayNotificationRepository.persist(entity);
    }

    @Transactional
    public @NonNull BirthdayNotificationDTO getNotification(long guildId, long userId) {

        BirthdayNotification entity = birthdayNotificationRepository
                .findByIdOptional(new BirthdayId(guildId, userId))
                .orElseThrow(()-> new BirthdayNotificationNotFoundException("No entry found for user: %s in guild: %s".formatted(userId, guildId)));

        return birthdayNotificationMapper.toDTO(entity);
    }

    @Transactional
    public @NonNull List<BirthdayNotificationDTO> getPendingNotifications() {
        return birthdayNotificationMapper.toDTOList(birthdayNotificationRepository.findPendingNotifications());
    }

    @Transactional
    public void updateNotification(long guildId, long userId, int birthMonth, int birthDate, @NonNull String zoneIdString) {

        BirthdayNotification entity = birthdayNotificationRepository
                .findByIdOptional(new BirthdayId(guildId, userId))
                .orElseThrow(()-> new BirthdayNotificationNotFoundException("No entry found for user: %s in guild: %s".formatted(guildId, userId)));

        // dirty checking
        entity.setNextNotificationAt(calculateNextNotificationAt(birthMonth, birthDate, zoneIdString));
    }

    // after a successful notification by the bot, call this method to set the event for next year
    @Transactional
    public void updateNotificationForNextYear(long guildId, long userId, @NonNull String zoneIdString) {

        BirthdayNotification entity = birthdayNotificationRepository
                .findByIdOptional(new BirthdayId(guildId, userId))
                .orElseThrow(()-> new BirthdayNotificationNotFoundException("No entry found for user: %s in guild: %s".formatted(guildId, userId)));

        ZoneId zoneId = ZoneId.of(zoneIdString);

        // dirty checking
        entity.setNextNotificationAt(
                entity.getNextNotificationAt().atZone(zoneId).plusYears(1).toInstant()
        );
    }

    @Transactional
    public void deleteNotification(long guildId, long userId) {
        if (!birthdayNotificationRepository.deleteById(new BirthdayId(guildId, userId)))
            throw new BirthdayNotificationNotFoundException("No entry found for user: %s in guild: %s".formatted(guildId, userId));
    }

    // for the given zoneId
    // if the birthdate has already passed for the current year, then set the notification to occur next year
    // else set it for this year's birthdate
    @NonNull Instant calculateNextNotificationAt(int birthMonth, int birthDay, @NonNull String zoneIdString) {

        // don't use time to compare cause
        // we want the notification to work as long as the birthdate day isn't over
        // this way, a user who enters their birthdate for the first time, on the day
        // of their birthday, will still be notified

        ZoneId zoneId = ZoneId.of(zoneIdString);
        int currentYear = Year.now(zoneId).getValue();
        int nextYear = currentYear + 1;

        LocalDate thisYearBirthDate = LocalDate.of(currentYear, birthMonth, birthDay);
        LocalDate presentDate = LocalDate.now(zoneId);

        return presentDate.isAfter(thisYearBirthDate) ?
                LocalDate.of(nextYear, birthMonth, birthDay).atStartOfDay(zoneId).toInstant() :
                LocalDate.of(currentYear, birthMonth, birthDay).atStartOfDay(zoneId).toInstant();
    }
}
