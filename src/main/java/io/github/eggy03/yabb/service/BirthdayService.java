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

        // calculate the upcoming birthday
        entity.setUpcomingBirthday(
                BirthdayUtils.calculateUpcomingBirthday(dto.getDay(), dto.getMonth(), dto.getZoneId())
        );

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
    public @NonNull List<BirthdayResponseDTO> getUpcomingBirthdays() {
        return birthdayResponseMapper.toDTOList(birthdayRepository.findUpcomingBirthdays());
    }

    @Transactional
    public @NonNull List<BirthdayResponseDTO> getActiveBirthdays() {
        return birthdayResponseMapper.toDTOList(birthdayRepository.findActiveBirthdays());
    }

    @Transactional
    public @NonNull List<BirthdayResponseDTO> getExpiredBirthdays() {
        return birthdayResponseMapper.toDTOList(birthdayRepository.findExpiredBirthdays());
    }

    @Transactional
    public void updateBirthday(@NonNull BirthdayRequestDTO updatedDTO) {

        Birthday entity =  birthdayRepository
                .findByIdOptional(new BirthdayId(updatedDTO.getGuildId(), updatedDTO.getUserId()))
                .orElseThrow(()-> new BirthdayNotFoundException("No entry found for user: %s in guild: %s".formatted(updatedDTO.getUserId(), updatedDTO.getGuildId())));

        // dirty check
        entity.setMonth(updatedDTO.getMonth());
        entity.setDay(updatedDTO.getDay());
        entity.setZoneId(updatedDTO.getZoneId());

        // re-calculate the upcoming birthday time
        entity.setUpcomingBirthday(BirthdayUtils.calculateUpcomingBirthday(updatedDTO.getDay(), updatedDTO.getMonth(), updatedDTO.getZoneId()));
    }

    @Transactional
    public void updateBirthdayForNextYear(@NonNull BirthdayRequestDTO dto) {

        Birthday entity = birthdayRepository
                .findByIdOptional(new BirthdayId(dto.getGuildId(), dto.getUserId()))
                .orElseThrow(()-> new BirthdayNotFoundException("No entry found for user: %s in guild: %s".formatted(dto.getGuildId(), dto.getUserId())));

        // get the latest birthday year and advance it by 1
        int year = entity.getUpcomingBirthday().atZone(ZoneId.of(entity.getZoneId())).getYear() + 1;

        // dirty checking
        entity.setUpcomingBirthday(
                // recalculate the birthday using the provided birthday and birthmonth but with the advanced year
                // never recalculate first and then advance the year cause it will lose leap day and month information
                // Example: Birthday: 29, Birthmonth: 2
                // Latest Year: 2027
                // To Advance To: 2028
                // If we run the util function on the latest year and then advance the year, it will change the date to 28 because 2027 is not a leap year
                // and the final date would be 28 Feb 2028. But we could have had 29 on 2028 cause it's a leap year. This is info loss.
                BirthdayUtils.calculateBirthdayForYear(entity.getDay(), entity.getMonth(), year, entity.getZoneId())
        );
    }

    @Transactional
    public void deleteBirthday(long guildId, long userId) {

        if (!birthdayRepository.deleteById(new BirthdayId(guildId, userId)))
            throw new BirthdayNotFoundException("No entry found for user: %s in guild: %s".formatted(guildId, userId)) ;
    }

    @Transactional
    public long deleteBirthdayByGuild(long guildId) {
        return birthdayRepository.deleteByGuildId(guildId);
    }
}
