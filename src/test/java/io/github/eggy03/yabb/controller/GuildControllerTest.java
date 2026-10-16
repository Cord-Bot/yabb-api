package io.github.eggy03.yabb.controller;

import io.github.eggy03.yabb.dto.GuildDTO;
import io.github.eggy03.yabb.entity.Guild;
import io.github.eggy03.yabb.repository.GuildRepository;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import jakarta.inject.Inject;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class GuildControllerTest {

    private static final String BASE_PATH = "/api/v1/guild";

    // sample data
    private static final long GUILD_ID = 111111111111111L;
    private static final long CHANNEL_ID = 222222222222222L;
    private static final long ROLE_ID = 333333333333333L;

    private static final long UPDATED_CHANNEL_ID = 444444444444444L;
    private static final long UPDATED_ROLE_ID = 555555555555555L;

    static @NonNull GuildDTO validDTO() {
        return new GuildDTO(GUILD_ID, CHANNEL_ID, ROLE_ID);
    }

    static @NonNull Stream<GuildDTO> validDTOs() {
        return Stream.of(validDTO(), new GuildDTO(GUILD_ID, CHANNEL_ID, null));
    }

    static @NonNull Stream<GuildDTO> validUpdatedDTOs() {
        return Stream.of(
                new GuildDTO(GUILD_ID, UPDATED_CHANNEL_ID, UPDATED_ROLE_ID),
                new GuildDTO(GUILD_ID, UPDATED_CHANNEL_ID, null)
        );
    }

    static @NonNull Stream<GuildDTO> negativeDTOs() {
        return Stream.of(
                new GuildDTO(-GUILD_ID, CHANNEL_ID, ROLE_ID),
                new GuildDTO(GUILD_ID, -CHANNEL_ID, ROLE_ID),
                new GuildDTO(GUILD_ID, CHANNEL_ID, -ROLE_ID)
        );
    }

    static @NonNull Stream<GuildDTO> nullDTOs() {
        return Stream.of(
                new GuildDTO(null, CHANNEL_ID, ROLE_ID),
                new GuildDTO(GUILD_ID, null, ROLE_ID)
        );
    }

    // inj deps
    @Inject
    GuildRepository guildRepository;

    // repeated rest-assured calls
    ValidatableResponse postGuild(@NonNull GuildDTO dto) {
        return given().contentType("application/json")
                .body(dto)
                .when()
                .post(BASE_PATH)
                .then();
    }

    ValidatableResponse getGuild(long guildId) {
        return given().contentType("application/json")
                .when()
                .get(BASE_PATH + "/" + guildId)
                .then();
    }

    ValidatableResponse patchGuild(@NonNull GuildDTO dto) {
        return given().contentType("application/json")
                .body(dto)
                .when()
                .patch(BASE_PATH)
                .then();
    }

    ValidatableResponse deleteGuild(long guildId) {
        return given().contentType("application/json")
                .when()
                .delete(BASE_PATH + "/" + guildId)
                .then();
    }

    // repeated assertion calls
    void assertAndValidateGuildExistsWith(@NonNull GuildDTO dto) {

        Optional<Guild> entityOptional = QuarkusTransaction
                .requiringNew()
                .call(() -> guildRepository.findByIdOptional(dto.getGuildId()));

        assertThat(entityOptional)
                .isPresent()
                .get()
                .satisfies(guild -> {
                    assertThat(guild.getGuildId()).isEqualTo(dto.getGuildId());
                    assertThat(guild.getChannelId()).isEqualTo(dto.getChannelId());
                    assertThat(guild.getRoleId()).isEqualTo(dto.getRoleId());
                });

    }

    void assertGuildDoesNotExistWith(long guildId) {

        Optional<Guild> entityOptional = QuarkusTransaction
                .requiringNew()
                .call(() -> guildRepository.findByIdOptional(guildId));

        assertThat(entityOptional).isEmpty();
    }

    void assertRepositoryEmpty() {

        List<Guild> allGuilds = QuarkusTransaction
                .requiringNew()
                .call(()-> guildRepository.findAll().list());

        assertThat(allGuilds).isEmpty();
    }

    // delete repository before each test
    @BeforeEach
    void cleanState() {
        QuarkusTransaction.requiringNew().run(guildRepository::deleteAll);
    }

    // actual tests
    @ParameterizedTest
    @MethodSource("validDTOs")
    void saveGuild_success(GuildDTO dto) {

        // save guild
        postGuild(dto).statusCode(201);

        // validate the saved guild
        assertAndValidateGuildExistsWith(dto);
    }

    @ParameterizedTest
    @MethodSource("validDTOs")
    void saveGuild_alreadyExists_conflicts(GuildDTO dto) {
        // save a guild once
        postGuild(dto).statusCode(201);

        // save again and expect 409
        postGuild(dto).statusCode(409);
    }

    @ParameterizedTest
    @MethodSource({"negativeDTOs", "nullDTOs"})
    void saveGuild_negativeAndNullDTOs_validationFails_badRequest(GuildDTO dto) {
        postGuild(dto).statusCode(400);

        assertRepositoryEmpty();
    }

    @Test
    void viewGuild_success() {
        // save a guild once
        postGuild(validDTO()).statusCode(201);

        // view the guild
        GuildDTO guildDTO = getGuild(validDTO().getGuildId())
                .statusCode(200)
                .extract()
                .body()
                .as(GuildDTO.class);

        // assert that it matches the guild we wanted to save
        assertThat(guildDTO).isEqualTo(validDTO());
    }

    @Test
    void viewGuild_notRegistered_notFound() {
        getGuild(validDTO().getGuildId()).statusCode(404);
    }

    @Test
    void viewGuild_invalidParameters_badRequest() {
        getGuild(-GUILD_ID).statusCode(400);
    }

    @ParameterizedTest
    @MethodSource("validUpdatedDTOs")
    void updateGuild_success(GuildDTO updatedDTO) {

        // save a guild once
        postGuild(validDTO()).statusCode(201);

        // update the guild
        patchGuild(updatedDTO).statusCode(204);

        // assert the guild was updated
        assertAndValidateGuildExistsWith(updatedDTO);
    }

    @ParameterizedTest
    @MethodSource("validUpdatedDTOs")
    void updateGuild_doesNotExist(GuildDTO updatedDTO) {

        // update the guild without saving first
        patchGuild(updatedDTO).statusCode(404);

        // assert the guild doesnt exist
        assertGuildDoesNotExistWith(updatedDTO.getGuildId());
    }


    @ParameterizedTest
    @MethodSource({"negativeDTOs", "nullDTOs"})
    void updateGuild_negativeAndNullDTOs_validationFails_badRequest(GuildDTO dto) {

        // save a valid guild once
        postGuild(validDTO()).statusCode(201);

        // try updating that guild (we note that validDTO.guildId = negative/nullDTO.guildId)
        // pls check at the top if this is true or else most of the tests will probably fail or have false positives
        patchGuild(dto).statusCode(400);

        // assert that the saved guild was unchanged
        assertAndValidateGuildExistsWith(validDTO());
    }

    @Test
    void deleteGuild_success() {
        // save a guild once
        postGuild(validDTO()).statusCode(201);

        // delete guild
        deleteGuild(validDTO().getGuildId()).statusCode(204);

        // assert does not exist
        assertGuildDoesNotExistWith(validDTO().getGuildId());
    }

    @Test
    void deleteGuild_doesNotExist_notFound() {
        deleteGuild(validDTO().getGuildId()).statusCode(404);
    }

    @Test
    void deleteGuild_invalidParameters_badRequest() {
        deleteGuild(-validDTO().getGuildId()).statusCode(400);
    }

}
