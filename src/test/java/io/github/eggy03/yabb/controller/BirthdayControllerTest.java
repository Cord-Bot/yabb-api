package io.github.eggy03.yabb.controller;

import io.github.eggy03.yabb.dto.BirthdayDTO;
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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@QuarkusTest
class BirthdayControllerTest {

    private static final String BASE_PATH = "/api/v1/birthday";

    // sample data
    private static final long GUILD_ID = 111111111111111L;
    private static final long USER_ID = 222222222222222L;
    private static final int MONTH = 1;
    private static final int DAY = 1;
    private static final String ZONE_ID = "Asia/Kolkata";

    private static final long GUILD_ID_TWO = 333333333333333L;
    private static final long USER_ID_TWO = 444444444444444L;
    private static final int MONTH_TWO = 9;
    private static final int DAY_TWO = 9;
    private static final String ZONE_ID_TWO = "America/New_York";

    private static final int UPDATED_MONTH = 2;
    private static final int UPDATED_DAY = 2;
    private static final String UPDATED_ZONE_ID = "Asia/Kolkata";

    // user 1 saves in guild 1
    static @NonNull BirthdayDTO validDTOOne() {
        return new BirthdayDTO(GUILD_ID, USER_ID, MONTH, DAY, ZONE_ID);
    }

    // user 1 saves in guild 2
    static @NonNull BirthdayDTO validDTOTwo() {
        return new BirthdayDTO(GUILD_ID_TWO, USER_ID, MONTH, DAY, ZONE_ID);

    }

    // user 2 saves in guild 1
    static @NonNull BirthdayDTO validDTOThree() {
        return new BirthdayDTO(GUILD_ID, USER_ID_TWO, MONTH_TWO, DAY_TWO, ZONE_ID_TWO);
    }

    // user 2 saves in guild 2
    static @NonNull BirthdayDTO validDTOFour() {
        return new BirthdayDTO(GUILD_ID_TWO, USER_ID_TWO, MONTH_TWO, DAY_TWO, ZONE_ID_TWO);
    }

    static @NonNull BirthdayDTO validUpdatedDTOOne() {
        return new BirthdayDTO(GUILD_ID, USER_ID, UPDATED_MONTH, UPDATED_DAY, UPDATED_ZONE_ID);
    }

    static @NonNull Stream<BirthdayDTO> validDTOs() {
        return Stream.of(validDTOOne(), validDTOTwo(), validDTOThree(), validDTOFour());
    }

    static @NonNull Stream<BirthdayDTO> negativeDTOs() {
        return Stream.of(
                new BirthdayDTO(-GUILD_ID, USER_ID, MONTH, DAY, ZONE_ID),
                new BirthdayDTO(GUILD_ID, -USER_ID, MONTH, DAY, ZONE_ID),
                new BirthdayDTO(GUILD_ID, USER_ID, -MONTH, DAY, ZONE_ID),
                new BirthdayDTO(GUILD_ID, USER_ID, MONTH, -DAY, ZONE_ID)
        );
    }

    static @NonNull Stream<BirthdayDTO> nullDTOs() {
        return Stream.of(
                new BirthdayDTO(null, USER_ID, MONTH, DAY, ZONE_ID),
                new BirthdayDTO(GUILD_ID, null, MONTH, DAY, ZONE_ID),
                new BirthdayDTO(GUILD_ID, USER_ID, null, DAY, ZONE_ID),
                new BirthdayDTO(GUILD_ID, USER_ID, MONTH, null, ZONE_ID),
                new BirthdayDTO(GUILD_ID, USER_ID, MONTH, DAY, null)
        );
    }

    // inj deps
    @Inject
    BirthdayRepository birthdayRepository;

    @Inject
    BirthdayScheduleRepository birthdayScheduleRepository;

    @Inject
    BirthdayScheduleCalculator birthdayScheduleCalculator;

    // repeated rest-assured calls
    ValidatableResponse postBirthday(@NonNull BirthdayDTO dto) {
        return given().contentType("application/json")
                .body(dto)
                .when()
                .post(BASE_PATH)
                .then();
    }

    ValidatableResponse getBirthday(long guildId, long userId) {
        return given().contentType("application/json")
                .when()
                .get(BASE_PATH + "/" + guildId + "/" + userId)
                .then();
    }

    ValidatableResponse getBirthdays(long guildId) {
        return given().contentType("application/json")
                .when()
                .get(BASE_PATH + "/" + guildId)
                .then();
    }

    ValidatableResponse patchBirthday(@NonNull BirthdayDTO dto) {
        return given().contentType("application/json")
                .body(dto)
                .when()
                .patch(BASE_PATH)
                .then();
    }

    ValidatableResponse deleteBirthday(long guildId, long userId) {
        return given().contentType("application/json")
                .when()
                .delete(BASE_PATH + "/" + guildId + "/" + userId)
                .then();
    }

    ValidatableResponse deleteBirthdays(long guildId) {
        return given().contentType("application/json")
                .when()
                .delete(BASE_PATH + "/" + guildId)
                .then();
    }

    // repeated assertion calls
    void assertAndValidateBirthdayExistsWith(@NonNull BirthdayDTO dto) {

        Optional<Birthday> entityOptional = QuarkusTransaction
                .requiringNew()
                .call(() -> birthdayRepository.findByIdOptional(new BirthdayId(dto.getGuildId(), dto.getUserId())));

        assertThat(entityOptional)
                .isPresent()
                .get()
                .satisfies(birthday -> {
                    assertThat(birthday.getGuildId()).isEqualTo(dto.getGuildId());
                    assertThat(birthday.getUserId()).isEqualTo(dto.getUserId());
                    assertThat(birthday.getMonth()).isEqualTo(dto.getMonth());
                    assertThat(birthday.getDay()).isEqualTo(dto.getDay());
                    assertThat(birthday.getZoneId()).isEqualTo(dto.getZoneId());
                });

    }

    void assertAndValidateBirthdayScheduleExistsWith(@NonNull BirthdayDTO dto) {

        Optional<BirthdaySchedule> entityOptional = QuarkusTransaction
                .requiringNew()
                .call(() -> birthdayScheduleRepository.findByIdOptional(new BirthdayId(dto.getGuildId(), dto.getUserId())));

        assertThat(entityOptional)
                .isPresent()
                .get()
                .satisfies(schedule -> {
                    assertThat(schedule.getGuildId()).isEqualTo(dto.getGuildId());
                    assertThat(schedule.getUserId()).isEqualTo(dto.getUserId());
                    assertThat(schedule.getUpcomingBirthdayAt()).isEqualTo(birthdayScheduleCalculator.calculateUpcomingBirthday(dto.getDay(), dto.getMonth(), dto.getZoneId()));
                });

        // Note: The HTTP request for saving/updating the birthday
        // and this validation will execute at different instants.

        // Both the HTTP request and this assertion use calculateUpcomingBirthday()
        // at their respective instants to validate the entry in the schedule.

        // Say someone's birthday is on July 6, and they set their birthday on July 6, 2026, at 23:59:59
        // The HTTP request runs and determines July 6, 2026, to be the upcoming birthday value,
        // since the birthday day hasn't passed yet.
        // But say, this validation runs on July 7, 2026, at 00:00:03,
        // This means the upcoming birthday for validation will be July 6, 2027.

        // This is because the utility function LocalDate.now() to calculate the present year
        // which drives its decision to set the upcoming birthday.
        // So, theoretically there can be cases where the assertion will assert for two different
        // upcoming birthday values at around midnight, which will cause the test to fail.

        // Mocking the util method is not an option cause I don't know how Quarkus
        // will handle HTTP requests. The two mocked instances of the utility
        // may not even run on the same thread.

        // Freeze time in tests to make the scheduled birthday assertion deterministic.
        // until then, don't validate the scheduled time

        // UPDATE: FixedClockProducer now injects a frozen clock
        // So every service which uses Clock will now use that fixed clock

    }

    void assertBirthdayDoesNotExistWith(long guildId, long userId) {

        Optional<Birthday> entityOptional = QuarkusTransaction
                .requiringNew()
                .call(() -> birthdayRepository.findByIdOptional(new BirthdayId(guildId, userId)));

        assertThat(entityOptional).isEmpty();
    }

    void assertBirthdayScheduleDoesNotExistWith(long guildId, long userId) {

        Optional<BirthdaySchedule> entityOptional = QuarkusTransaction
                .requiringNew()
                .call(() -> birthdayScheduleRepository.findByIdOptional(new BirthdayId(guildId, userId)));

        assertThat(entityOptional).isEmpty();
    }

    void assertBirthdayRepositoryEmpty() {

        List<Birthday> birthdayList = QuarkusTransaction
                .requiringNew()
                .call(()-> birthdayRepository.findAll().list());

        assertThat(birthdayList).isEmpty();
    }

    void assertBirthdayScheduleRepositoryEmpty() {

        List<BirthdaySchedule> birthdayScheduleList = QuarkusTransaction
                .requiringNew()
                .call(()-> birthdayScheduleRepository.findAll().list());

        assertThat(birthdayScheduleList).isEmpty();
    }

    // delete repository before each test
    @BeforeEach
    void cleanState() {
        // right now, schedule repo has on cascade delete set
        // so deleting birthday also deletes the schedule repo
        QuarkusTransaction.requiringNew().run(birthdayRepository::deleteAll);
    }

    // actual tests
    @ParameterizedTest
    @MethodSource("validDTOs")
    void saveBirthday_success(BirthdayDTO dto) {

        // save birthday
        postBirthday(dto).statusCode(201);

        // validate the saved birthday and schedule
        assertAndValidateBirthdayExistsWith(dto);
        assertAndValidateBirthdayScheduleExistsWith(dto);
    }

    @Test
    void saveBirthday_alreadyExists_conflicts() {
        // save a birthday once
        postBirthday(validDTOOne()).statusCode(201);

        // save again and expect 409
        // we're using updated dto cause it will still conflict as the birthdayId (guildId + channelId)
        // are the same for these two, while the rest of the fields are different
        // this way we can also assure that the saved one remains unchanged
        postBirthday(validUpdatedDTOOne()).statusCode(409);

        // verify the original birthday and schedule remain intact
        assertAndValidateBirthdayExistsWith(validDTOOne());
        assertAndValidateBirthdayScheduleExistsWith(validDTOOne());
    }

    @ParameterizedTest
    @MethodSource({"negativeDTOs", "nullDTOs"})
    void saveBirthday_negativeAndNullDTOs_validationFails_badRequest(BirthdayDTO dto) {
        postBirthday(dto).statusCode(400);

        assertBirthdayRepositoryEmpty();
        assertBirthdayScheduleRepositoryEmpty();
    }

    @Test
    void getBirthday_success() {

        // save a birthday once
        postBirthday(validDTOOne()).statusCode(201);

        // view the birthday
        BirthdayDTO dto = getBirthday(validDTOOne().getGuildId(), validDTOOne().getUserId())
                .statusCode(200)
                .extract()
                .body()
                .as(BirthdayDTO.class);

        // assert that it matches the guild we wanted to save
        assertThat(dto).isEqualTo(validDTOOne());
    }

    @Test
    void getBirthdays_success_found() {

        // save three birthdays with two belonging to the same guildId
        // guild 1
        postBirthday(validDTOOne()).statusCode(201);
        postBirthday(validDTOThree()).statusCode(201);
        // guild 2
        postBirthday(validDTOTwo()).statusCode(201);

        // get the birthday list for guild 1
        List<BirthdayDTO> dtoList = getBirthdays(validDTOOne().getGuildId())
                .statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {});

        // assert that it matches the birthdays of guild 1
        assertThat(dtoList).isEqualTo(List.of(validDTOOne(), validDTOThree()));
    }

    @Test
    void getBirthdays_success_empty() {

        // get the birthday list without saving any
        List<BirthdayDTO> dtoList = getBirthdays(validDTOOne().getGuildId())
                .statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {});

        // assert that it matches the birthdays of guild 1
        assertThat(dtoList).isEmpty();
    }

    @Test
    void getBirthday_notRegistered_notFound() {
        getBirthday(validDTOOne().getGuildId(), validDTOOne().getUserId())
                .statusCode(404);
    }

    @Test
    void getBirthday_invalidParameters_badRequest() {
        getBirthday(-GUILD_ID, USER_ID).statusCode(400);
        getBirthday(GUILD_ID, -USER_ID).statusCode(400);
    }

    @Test
    void updateBirthday_success() {

        // save a birthday once
        postBirthday(validDTOOne()).statusCode(201);

        // validate the birthday and the schedule
        assertAndValidateBirthdayExistsWith(validDTOOne());
        assertAndValidateBirthdayScheduleExistsWith(validDTOOne());

        // update the birthday having the same id
        patchBirthday(validUpdatedDTOOne()).statusCode(204);

        // validate the updated birthday and schedule
        assertAndValidateBirthdayExistsWith(validUpdatedDTOOne());
        assertAndValidateBirthdayScheduleExistsWith(validUpdatedDTOOne());

        // we note that validDTOOne and validUpdatedDTOOne both have the same birthdayID (guildId + userId)
        // or else the test would throw errors or worse, be false positive
    }

    @Test
    void updateBirthday_doesNotExist() {

        // update the birthday without saving first
        patchBirthday(validUpdatedDTOOne()).statusCode(404);

        // assert the birthday and the schedule doesn't exist
        assertBirthdayDoesNotExistWith(validUpdatedDTOOne().getGuildId(), validUpdatedDTOOne().getUserId());
        assertBirthdayScheduleDoesNotExistWith(validUpdatedDTOOne().getGuildId(), validUpdatedDTOOne().getUserId());
    }


    @ParameterizedTest
    @MethodSource({"negativeDTOs", "nullDTOs"})
    void updateBirthday_negativeAndNullDTOs_validationFails_badRequest(BirthdayDTO dto) {

        // save a valid birthday once
        postBirthday(validDTOOne()).statusCode(201);

        // we note that validDTOOne and validUpdatedDTOOne both have the same birthdayID (guildId + userId)
        // or else the test would throw errors or worse, be false positive
        // pls check at the top if this is true or else most of the tests will probably fail or have false positives
        patchBirthday(dto).statusCode(400);

        // assert that the saved birthday and schedule was unchanged
        assertAndValidateBirthdayExistsWith(validDTOOne());
        assertAndValidateBirthdayScheduleExistsWith(validDTOOne());
    }

    @Test
    void deleteBirthday_success() {
        // save a birthday once
        postBirthday(validDTOOne()).statusCode(201);

        // validate the saved birthday and schedule
        assertAndValidateBirthdayExistsWith(validDTOOne());
        assertAndValidateBirthdayScheduleExistsWith(validDTOOne());

        // delete birthday
        deleteBirthday(validDTOOne().getGuildId(), validDTOOne().getUserId()).statusCode(204);

        // assert the birthday and the schedule doesn't exist
        assertBirthdayDoesNotExistWith(validDTOOne().getGuildId(), validDTOOne().getUserId());
        assertBirthdayScheduleDoesNotExistWith(validDTOOne().getGuildId(), validDTOOne().getUserId());
    }

    @Test
    void deleteBirthdays_success() {

        // save three birthdays with two belonging to the same guildId
        // guild 1
        postBirthday(validDTOOne()).statusCode(201);
        postBirthday(validDTOThree()).statusCode(201);
        // guild 2
        postBirthday(validDTOTwo()).statusCode(201);

        // validate the saved birthdays and schedules
        assertAndValidateBirthdayExistsWith(validDTOOne());
        assertAndValidateBirthdayScheduleExistsWith(validDTOOne());

        assertAndValidateBirthdayExistsWith(validDTOTwo());
        assertAndValidateBirthdayScheduleExistsWith(validDTOTwo());

        assertAndValidateBirthdayExistsWith(validDTOThree());
        assertAndValidateBirthdayScheduleExistsWith(validDTOThree());

        // delete birthdays belonging to guild 1
        String deleteCount = deleteBirthdays(validDTOOne().getGuildId())
                .statusCode(200)
                .extract()
                .body()
                .as(String.class);

        assertThat(deleteCount).isEqualTo("2");

        // assert the birthdays and the schedules doesn't exist for guild 1 but exists for guild 2
        assertBirthdayDoesNotExistWith(validDTOOne().getGuildId(), validDTOOne().getUserId());
        assertBirthdayScheduleDoesNotExistWith(validDTOOne().getGuildId(), validDTOOne().getUserId());

        assertBirthdayDoesNotExistWith(validDTOThree().getGuildId(), validDTOThree().getUserId());
        assertBirthdayScheduleDoesNotExistWith(validDTOThree().getGuildId(), validDTOThree().getUserId());

        assertAndValidateBirthdayExistsWith(validDTOTwo());
        assertAndValidateBirthdayScheduleExistsWith(validDTOTwo());
    }

    @Test
    void deleteBirthday_doesNotExist_notFound() {
        deleteBirthday(validDTOOne().getGuildId(), validDTOOne().getUserId())
                .statusCode(404);
    }

    @Test
    void deleteBirthday_invalidParameters_badRequest() {
        deleteBirthday(-validDTOOne().getGuildId(), validDTOOne().getUserId()).statusCode(400);
        deleteBirthday(validDTOOne().getGuildId(), -validDTOOne().getUserId()).statusCode(400);
    }
}
