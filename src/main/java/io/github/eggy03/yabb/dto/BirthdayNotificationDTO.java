package io.github.eggy03.yabb.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BirthdayNotificationDTO {

    @NotNull(message = "GuildID cannot be null")
    @Positive(message = "GuildID must be positive")
    private Long guildId;

    @NotNull(message = "UserID cannot be null")
    @Positive(message = "UserID must be positive")
    private Long userId;

    @NotNull(message = "nextNotificationAt cannot be null")
    private Instant nextNotificationAt;
}
