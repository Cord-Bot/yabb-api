package io.github.eggy03.yabb.controller;

import io.github.eggy03.yabb.dto.ConfigDTO;
import io.github.eggy03.yabb.entity.Config;
import io.github.eggy03.yabb.repository.ConfigRepository;
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
class ConfigControllerTest {

    private static final String BASE_PATH = "/api/v1/config";

    // sample data
    private static final long GUILD_ID = 111111111111111L;
    private static final long CHANNEL_ID = 222222222222222L;
    private static final long ROLE_ID = 333333333333333L;

    private static final long UPDATED_CHANNEL_ID = 444444444444444L;
    private static final long UPDATED_ROLE_ID = 555555555555555L;

    static @NonNull ConfigDTO validDTO() {
        return new ConfigDTO(GUILD_ID, CHANNEL_ID, ROLE_ID);
    }

    static @NonNull Stream<ConfigDTO> validDTOs() {
        return Stream.of(validDTO(), new ConfigDTO(GUILD_ID, CHANNEL_ID, null));
    }

    static @NonNull Stream<ConfigDTO> validUpdatedDTOs() {
        return Stream.of(
                new ConfigDTO(GUILD_ID, UPDATED_CHANNEL_ID, UPDATED_ROLE_ID),
                new ConfigDTO(GUILD_ID, UPDATED_CHANNEL_ID, null)
        );
    }

    static @NonNull Stream<ConfigDTO> negativeDTOs() {
        return Stream.of(
                new ConfigDTO(-GUILD_ID, CHANNEL_ID, ROLE_ID),
                new ConfigDTO(GUILD_ID, -CHANNEL_ID, ROLE_ID),
                new ConfigDTO(GUILD_ID, CHANNEL_ID, -ROLE_ID)
        );
    }

    static @NonNull Stream<ConfigDTO> nullDTOs() {
        return Stream.of(
                new ConfigDTO(null, CHANNEL_ID, ROLE_ID),
                new ConfigDTO(GUILD_ID, null, ROLE_ID)
        );
    }

    // inj deps
    @Inject
    ConfigRepository configRepository;

    // repeated rest-assured calls
    ValidatableResponse postConfig(@NonNull ConfigDTO dto) {
        return given().contentType("application/json")
                .body(dto)
                .when()
                .post(BASE_PATH)
                .then();
    }

    ValidatableResponse getConfig(long guildId) {
        return given().contentType("application/json")
                .when()
                .get(BASE_PATH + "/" + guildId)
                .then();
    }

    ValidatableResponse patchConfig(@NonNull ConfigDTO dto) {
        return given().contentType("application/json")
                .body(dto)
                .when()
                .patch(BASE_PATH)
                .then();
    }

    ValidatableResponse deleteConfig(long guildId) {
        return given().contentType("application/json")
                .when()
                .delete(BASE_PATH + "/" + guildId)
                .then();
    }

    // repeated assertion calls
    void assertAndValidateConfigExistsWith(@NonNull ConfigDTO dto) {

        Optional<Config> entityOptional = QuarkusTransaction
                .requiringNew()
                .call(() -> configRepository.findByIdOptional(dto.getGuildId()));

        assertThat(entityOptional)
                .isPresent()
                .get()
                .satisfies(config -> {
                    assertThat(config.getGuildId()).isEqualTo(dto.getGuildId());
                    assertThat(config.getChannelId()).isEqualTo(dto.getChannelId());
                    assertThat(config.getRoleId()).isEqualTo(dto.getRoleId());
                });

    }

    void assertConfigDoesNotExistWith(long guildId) {

        Optional<Config> entityOptional = QuarkusTransaction
                .requiringNew()
                .call(() -> configRepository.findByIdOptional(guildId));

        assertThat(entityOptional).isEmpty();
    }

    void assertRepositoryEmpty() {

        List<Config> allConfigs = QuarkusTransaction
                .requiringNew()
                .call(()-> configRepository.findAll().list());

        assertThat(allConfigs).isEmpty();
    }

    // delete repository before each test
    @BeforeEach
    void cleanState() {
        QuarkusTransaction.requiringNew().run(configRepository::deleteAll);
    }

    // actual tests
    @ParameterizedTest
    @MethodSource("validDTOs")
    void saveConfig_success(ConfigDTO dto) {

        // save config
        postConfig(dto).statusCode(201);

        // validate the saved config
        assertAndValidateConfigExistsWith(dto);
    }

    @ParameterizedTest
    @MethodSource("validDTOs")
    void saveConfig_alreadyExists_conflicts(ConfigDTO dto) {
        // save a config once
        postConfig(dto).statusCode(201);

        // save again and expect 409
        postConfig(dto).statusCode(409);
    }

    @ParameterizedTest
    @MethodSource({"negativeDTOs", "nullDTOs"})
    void saveConfig_negativeAndNullDTOs_validationFails_badRequest(ConfigDTO dto) {
        postConfig(dto).statusCode(400);

        assertRepositoryEmpty();
    }

    @Test
    void getConfig_success() {
        // save a config once
        postConfig(validDTO()).statusCode(201);

        // view the config
        ConfigDTO configDTO = getConfig(validDTO().getGuildId())
                .statusCode(200)
                .extract()
                .body()
                .as(ConfigDTO.class);

        // assert that it matches the config we wanted to save
        assertThat(configDTO).isEqualTo(validDTO());
    }

    @Test
    void getConfig_notRegistered_notFound() {
        getConfig(validDTO().getGuildId()).statusCode(404);
    }

    @Test
    void getConfig_invalidParameters_badRequest() {
        getConfig(-GUILD_ID).statusCode(400);
    }

    @ParameterizedTest
    @MethodSource("validUpdatedDTOs")
    void updateConfig_success(ConfigDTO updatedDTO) {

        // save a config once
        postConfig(validDTO()).statusCode(201);

        // update the config
        patchConfig(updatedDTO).statusCode(204);

        // assert the config was updated
        assertAndValidateConfigExistsWith(updatedDTO);
    }

    @ParameterizedTest
    @MethodSource("validUpdatedDTOs")
    void updateConfig_doesNotExist(ConfigDTO updatedDTO) {

        // update the config without saving first
        patchConfig(updatedDTO).statusCode(404);

        // assert the config doesnt exist
        assertConfigDoesNotExistWith(updatedDTO.getGuildId());
    }


    @ParameterizedTest
    @MethodSource({"negativeDTOs", "nullDTOs"})
    void updateConfig_negativeAndNullDTOs_validationFails_badRequest(ConfigDTO dto) {

        // save a valid config once
        postConfig(validDTO()).statusCode(201);

        // try updating that config (we note that validDTOOne.guildId = negative/nullDTO.guildId)
        // pls check at the top if this is true or else most of the tests will probably fail or have false positives
        patchConfig(dto).statusCode(400);

        // assert that the saved config was unchanged
        assertAndValidateConfigExistsWith(validDTO());
    }

    @Test
    void deleteConfig_success() {
        // save a config once
        postConfig(validDTO()).statusCode(201);

        // delete config
        deleteConfig(validDTO().getGuildId()).statusCode(204);

        // assert does not exist
        assertConfigDoesNotExistWith(validDTO().getGuildId());
    }

    @Test
    void deleteConfig_doesNotExist_notFound() {
        deleteConfig(validDTO().getGuildId()).statusCode(404);
    }

    @Test
    void deleteConfig_invalidParameters_badRequest() {
        deleteConfig(-validDTO().getGuildId()).statusCode(400);
    }

}
