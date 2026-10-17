package io.github.eggy03.yabb.controller;

import io.github.eggy03.yabb.dto.BirthdayScheduleDTO;
import io.github.eggy03.yabb.entity.Birthday;
import io.github.eggy03.yabb.entity.BirthdayId;
import io.github.eggy03.yabb.entity.BirthdaySchedule;
import io.github.eggy03.yabb.repository.BirthdayRepository;
import io.github.eggy03.yabb.repository.BirthdayScheduleRepository;
import io.github.eggy03.yabb.utility.BirthdayScheduleCalculator;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import jakarta.inject.Inject;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class BirthdayScheduleControllerTest {

    // inj deps
    @Inject
    BirthdayRepository birthdayRepository;

    @Inject
    BirthdayScheduleRepository birthdayScheduleRepository;

    @Inject
    BirthdayScheduleCalculator birthdayScheduleCalculator;

    private static final String BASE_PATH = "/api/v1/birthday/schedule";

    // sample data
    private static final long GUILD_ID_ONE = 111111111111111L;
    private static final long USER_ID_ONE = 222222222222222L;

    private static final long GUILD_ID_TWO = 333333333333333L;
    private static final long USER_ID_TWO = 444444444444444L;

    // must be the same as the value provided by FixedClockProducer
    // since this is a static field, we cannot use the value from our FixedClockProducer bean
    // so we use this instead
    private static final Instant NOW = Instant.parse("2002-06-07T12:00:00Z");

    private static final Instant UPCOMING_INSTANT = NOW.plusSeconds(1);
    private static final Instant ACTIVE_INSTANT = NOW;
    private static final Instant EXPIRED_INSTANT = NOW.minusSeconds(86_401);

    // Upcoming schedule (user 1 in guild 1)
    static @NonNull BirthdayScheduleDTO upcomingDTO() {
        return new BirthdayScheduleDTO(
                GUILD_ID_ONE, USER_ID_ONE, UPCOMING_INSTANT);
    }

    // Active schedule (user 2 in guild 1)
    static @NonNull BirthdayScheduleDTO activeDTO() {
        return new BirthdayScheduleDTO(
                GUILD_ID_ONE, USER_ID_TWO, ACTIVE_INSTANT);
    }

    // Expired schedule (user 1 in guild 2)
    static @NonNull BirthdayScheduleDTO expiredDTO() {
        return new BirthdayScheduleDTO(
                GUILD_ID_TWO, USER_ID_ONE, EXPIRED_INSTANT);
    }

    static @NonNull Stream<BirthdayScheduleDTO> validDTOs() {
        return Stream.of(
                upcomingDTO(),
                activeDTO(),
                expiredDTO()
        );
    }

    // repeated persistence calls
    void persistSchedule(@NonNull BirthdayScheduleDTO dto) {
        QuarkusTransaction.requiringNew().run(() -> {
            birthdayRepository.persistAndFlush(
                    new Birthday(
                            dto.getGuildId(),
                            dto.getUserId(),
                            dto.getUpcomingBirthdayAt().atZone(ZoneId.of("Asia/Kolkata")).getMonthValue(),
                            dto.getUpcomingBirthdayAt().atZone(ZoneId.of("Asia/Kolkata")).getDayOfMonth(),
                            "Asia/Kolkata"));


            birthdayScheduleRepository.persistAndFlush(
                    new BirthdaySchedule(
                            dto.getGuildId(),
                            dto.getUserId(),
                            dto.getUpcomingBirthdayAt()));
        });
    }


    // repeated rest-assured calls
    ValidatableResponse getSchedule(long guildId, long userId) {
        return given().contentType("application/json")
                .when()
                .get(BASE_PATH + "/" + guildId + "/" + userId)
                .then();
    }

    ValidatableResponse getUpcomingSchedules() {
        return given().contentType("application/json")
                .when()
                .get(BASE_PATH + "/upcoming")
                .then();
    }

    ValidatableResponse getActiveSchedules() {
        return given().contentType("application/json")
                .when()
                .get(BASE_PATH + "/active")
                .then();
    }

    ValidatableResponse getExpiredSchedules() {
        return given().contentType("application/json")
                .when()
                .get(BASE_PATH + "/expired")
                .then();
    }

    ValidatableResponse patchSchedule(long guildId, long userId) {
        return given().contentType("application/json")
                .when()
                .patch(BASE_PATH + "/advance/" + guildId + "/" + userId)
                .then();
    }

    // repeated assertion calls
    void assertScheduleExistsWith(@NonNull BirthdayScheduleDTO dto) {
        Optional<BirthdaySchedule> entityOptional = QuarkusTransaction
                .requiringNew()
                .call(() -> birthdayScheduleRepository.findByIdOptional(
                        new BirthdayId(dto.getGuildId(), dto.getUserId())
                ));

        assertThat(entityOptional)
                .isPresent()
                .get()
                .satisfies(schedule -> {
                    assertThat(schedule.getGuildId()).isEqualTo(dto.getGuildId());
                    assertThat(schedule.getUserId()).isEqualTo(dto.getUserId());
                    assertThat(schedule.getUpcomingBirthdayAt())
                            .isEqualTo(dto.getUpcomingBirthdayAt());
                });
    }

    void assertScheduleDoesNotExistWith(long guildId, long userId) {
        Optional<BirthdaySchedule> entityOptional = QuarkusTransaction
                .requiringNew()
                .call(() -> birthdayScheduleRepository.findByIdOptional(
                        new BirthdayId(guildId, userId)
                ));

        assertThat(entityOptional).isEmpty();
    }

    // delete repository before each test
    @BeforeEach
    void cleanState() {
        // right now, schedule repo has on cascade delete set
        // so deleting birthday also deletes the schedule repo
        QuarkusTransaction.requiringNew().run(birthdayRepository::deleteAll);
    }

    @ParameterizedTest
    @MethodSource("validDTOs")
    void getSchedule_success(@NonNull BirthdayScheduleDTO dto) {

        persistSchedule(dto);

        BirthdayScheduleDTO savedScheduleDTO = getSchedule(dto.getGuildId(), dto.getUserId())
                .statusCode(200)
                .extract()
                .as(BirthdayScheduleDTO.class);

        assertScheduleExistsWith(savedScheduleDTO);
    }

    @ParameterizedTest
    @MethodSource("validDTOs")
    void getSchedule_notFound(@NonNull BirthdayScheduleDTO dto) {

        // don't save any birthdays or schedules
        getSchedule(dto.getGuildId(), dto.getUserId())
                .statusCode(404);

        assertScheduleDoesNotExistWith(dto.getGuildId(), dto.getUserId());
    }

    @Test
    void getUpcomingSchedules_success() {
        persistSchedule(upcomingDTO());
        persistSchedule(activeDTO());
        persistSchedule(expiredDTO());

        List<BirthdayScheduleDTO> response = getUpcomingSchedules()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {});

        assertThat(response).containsExactly(upcomingDTO());
    }

    @Test
    void getActiveSchedules_success() {
        persistSchedule(upcomingDTO());
        persistSchedule(activeDTO());
        persistSchedule(expiredDTO());

        List<BirthdayScheduleDTO> response = getActiveSchedules()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {});

        assertThat(response).containsExactly(activeDTO());
    }

    @Test
    void getExpiredSchedules_success() {
        persistSchedule(upcomingDTO());
        persistSchedule(activeDTO());
        persistSchedule(expiredDTO());

        List<BirthdayScheduleDTO> response = getExpiredSchedules()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {});

        assertThat(response).containsExactly(expiredDTO());
    }

    @Test
    void advanceSchedule_success() {
        // persist schedule
        persistSchedule(expiredDTO());

        // verify persistence
        assertScheduleExistsWith(expiredDTO());

        // call advance
        patchSchedule(expiredDTO().getGuildId(), expiredDTO().getUserId())
                .statusCode(204);

        // get the advanced schedule
        BirthdayScheduleDTO response = getSchedule(expiredDTO().getGuildId(), expiredDTO().getUserId())
                .statusCode(200)
                .extract()
                .as(BirthdayScheduleDTO.class);

        // formulate the expected next year birthday
        ZonedDateTime zonedDateTime = expiredDTO().getUpcomingBirthdayAt().atZone(ZoneId.of("Asia/Kolkata"));
        int day = zonedDateTime.getDayOfMonth();
        int month = zonedDateTime.getMonthValue();
        int advancedYear = zonedDateTime.getYear() + 1;

        Instant expectedNextYearBirthdayAt = birthdayScheduleCalculator
                .calculateBirthdayForYear(day, month, advancedYear, "Asia/Kolkata");

        // assert the formulation
        assertThat(response.getUpcomingBirthdayAt()).isEqualTo(expectedNextYearBirthdayAt);
    }

    @Test
    void advanceSchedule_noScheduleFound() {
        patchSchedule(expiredDTO().getGuildId(), expiredDTO().getUserId()).statusCode(404);
    }

    @Test
    void advanceSchedule_invalidParameters() {
        patchSchedule(-expiredDTO().getGuildId(), expiredDTO().getUserId()).statusCode(400);
        patchSchedule(expiredDTO().getGuildId(), -expiredDTO().getUserId()).statusCode(400);
    }
}
