package io.github.eggy03.yabb.service;

import io.github.eggy03.yabb.dto.BirthdayDTO;
import io.github.eggy03.yabb.dto.BirthdayScheduleDTO;
import io.github.eggy03.yabb.entity.BirthdayId;
import io.github.eggy03.yabb.entity.BirthdaySchedule;
import io.github.eggy03.yabb.exception.BirthdayScheduleNotFoundException;
import io.github.eggy03.yabb.mapper.BirthdayScheduleMapper;
import io.github.eggy03.yabb.repository.BirthdayScheduleRepository;
import io.github.eggy03.yabb.utility.BirthdayScheduleUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.ZoneId;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class BirthdayScheduleService {

    private final @NonNull BirthdayScheduleMapper birthdayScheduleMapper;
    private final @NonNull BirthdayScheduleRepository birthdayScheduleRepository;

    @Transactional
    public void saveSchedule(@NonNull BirthdayDTO dto) {
        // create schedule entity
        BirthdaySchedule birthdaySchedule = new BirthdaySchedule(dto.getGuildId(), dto.getUserId(), null);
        birthdaySchedule.setUpcomingBirthdayAt(BirthdayScheduleUtils.calculateUpcomingBirthday(dto.getDay(), dto.getMonth(), dto.getZoneId()));

        // persist schedule
        birthdayScheduleRepository.persist(birthdaySchedule);
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
    public void updateSchedule(@NonNull BirthdayDTO updatedDTO) {

        BirthdaySchedule birthdaySchedule = birthdayScheduleRepository
                .findByIdOptional(new BirthdayId(updatedDTO.getGuildId(), updatedDTO.getUserId()))
                .orElseThrow(()-> new BirthdayScheduleNotFoundException("No schedule found for user: [%s] in guild: [%s]".formatted(updatedDTO.getUserId(), updatedDTO.getGuildId())));

        // dirty update for schedule
        birthdaySchedule.setUpcomingBirthdayAt(
                BirthdayScheduleUtils.calculateUpcomingBirthday(updatedDTO.getDay(), updatedDTO.getMonth(), updatedDTO.getZoneId())
        );

    }

    @Transactional
    public void advanceSchedule(@NonNull BirthdayDTO dto) {

        BirthdaySchedule birthdaySchedule = birthdayScheduleRepository
                .findByIdOptional(new BirthdayId(dto.getGuildId(), dto.getUserId()))
                .orElseThrow(()-> new BirthdayScheduleNotFoundException("No entry found for user: [%s] in guild: [%s]".formatted(dto.getUserId(), dto.getGuildId())));

        // get the latest birthday year and advance it by 1
        int year = birthdaySchedule
                .getUpcomingBirthdayAt()
                .atZone(ZoneId.of(dto.getZoneId()))
                .getYear() + 1;

        // dirty update
        birthdaySchedule.setUpcomingBirthdayAt(
                // recalculate the birthday using the provided birthday and birthmonth but with the advanced year
                // never recalculate first and then advance the year cause it will lose leap day and month information
                // Example: Birthday: 29, Birthmonth: 2
                // Latest Year: 2027
                // To Advance To: 2028
                // If we run the util function on the latest year and then advance the year, it will change the date to 28 because 2027 is not a leap year
                // and the final date would be 28 Feb 2028. But we could have had 29 on 2028 cause it's a leap year. This is info loss.
                BirthdayScheduleUtils.calculateBirthdayForYear(dto.getDay(), dto.getMonth(), year, dto.getZoneId())
        );
    }
}
