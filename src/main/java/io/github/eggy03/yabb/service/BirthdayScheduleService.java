package io.github.eggy03.yabb.service;

import io.github.eggy03.yabb.dto.BirthdayScheduleDTO;
import io.github.eggy03.yabb.entity.Birthday;
import io.github.eggy03.yabb.entity.BirthdayId;
import io.github.eggy03.yabb.entity.BirthdaySchedule;
import io.github.eggy03.yabb.exception.BirthdayNotFoundException;
import io.github.eggy03.yabb.exception.BirthdayScheduleNotFoundException;
import io.github.eggy03.yabb.mapper.BirthdayScheduleMapper;
import io.github.eggy03.yabb.repository.BirthdayRepository;
import io.github.eggy03.yabb.repository.BirthdayScheduleRepository;
import io.github.eggy03.yabb.utility.BirthdayScheduleCalculator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.ZoneId;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class BirthdayScheduleService {

    private final @NonNull BirthdayScheduleCalculator birthdayScheduleCalculator;
    private final @NonNull BirthdayScheduleMapper birthdayScheduleMapper;
    private final @NonNull BirthdayScheduleRepository birthdayScheduleRepository;
    private final @NonNull BirthdayRepository birthdayRepository;

    @Transactional
    public BirthdayScheduleDTO getSchedule(long guildId, long userId) {

        BirthdaySchedule entity =  birthdayScheduleRepository
                .findByIdOptional(new BirthdayId(guildId, userId))
                .orElseThrow(()-> new BirthdayScheduleNotFoundException("No birthday schedule found for user: [%s] in guild: [%s]".formatted(userId, guildId)));

        return birthdayScheduleMapper.toDTO(entity);
    }

    @Transactional
    public @NonNull List<BirthdayScheduleDTO> getUpcomingSchedules() {
        return birthdayScheduleMapper.toDTOList(birthdayScheduleRepository.findUpcomingSchedules());
    }

    @Transactional
    public @NonNull List<BirthdayScheduleDTO> getActiveSchedules() {
        return birthdayScheduleMapper.toDTOList(birthdayScheduleRepository.findActiveSchedules());
    }

    @Transactional
    public @NonNull List<BirthdayScheduleDTO> getExpiredSchedules() {
        return birthdayScheduleMapper.toDTOList(birthdayScheduleRepository.findExpiredSchedules());
    }

    @Transactional
    public void advanceSchedule(long guildId, long userId) {

        // find the schedule
        BirthdaySchedule birthdaySchedule = birthdayScheduleRepository
                .findByIdOptional(new BirthdayId(guildId, userId))
                .orElseThrow(()-> new BirthdayScheduleNotFoundException("No birthday schedule found for user: [%s] in guild: [%s]".formatted(userId, guildId)));

        // find the birthday linked to this schedule
        Birthday birthday = birthdayRepository
                .findByIdOptional(new BirthdayId(guildId, userId))
                .orElseThrow(()-> new BirthdayNotFoundException("No birthday found for user: [%s] in guild: [%s]".formatted(userId, guildId)));

        int day = birthday.getDay();
        int month = birthday.getMonth();
        String timeZone = birthday.getZoneId();

        // get the latest year from the schedule and advance it by 1
        int nextYear = birthdaySchedule
                .getUpcomingBirthdayAt()
                .atZone(ZoneId.of(timeZone))
                .getYear() + 1;

        // update schedule via dirty checking
        birthdaySchedule.setUpcomingBirthdayAt(
                // recalculate the birthday using the provided birthday and birthmonth but with the advanced year
                // never recalculate first and then advance the year cause it will lose leap day and month information
                // Example: Birthday: 29, Birthmonth: 2
                // Latest Year: 2027
                // To Advance To: 2028
                // If we run the util function on the latest year and then advance the year, it will change the date to 28 because 2027 is not a leap year
                // and the final date would be 28 Feb 2028. But we could have had 29 on 2028 cause it's a leap year. This is info loss.
                birthdayScheduleCalculator.calculateBirthdayForYear(day, month, nextYear, timeZone)
        );
    }
}
