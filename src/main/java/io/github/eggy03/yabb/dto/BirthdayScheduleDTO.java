package io.github.eggy03.yabb.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BirthdayScheduleDTO {

    @NotNull(message = "GuildID cannot be null")
    @Positive(message = "GuildID must be positive")
    @Schema(required = true, description = "The guild ID of the birthday person", examples = "123456789012345678")
    private Long guildId;

    @NotNull(message = "UserID cannot be null")
    @Positive(message = "UserID must be positive")
    @Schema(required = true, description = "The user ID of the birthday person", examples = "987654321098765432")
    private Long userId;

    @NotNull(message = "upcomingBirthdayAt cannot be null")
    @Schema(required = true, description = "Upcoming birth date and time of the birthday person", examples = {"2027-08-28T18:30:00Z"})
    private Instant upcomingBirthdayAt;
}
