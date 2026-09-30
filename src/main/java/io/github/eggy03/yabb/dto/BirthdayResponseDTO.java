package io.github.eggy03.yabb.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BirthdayResponseDTO {

    @NotNull(message = "GuildID cannot be null")
    @Positive(message = "GuildID must be positive")
    private Long guildId;

    @NotNull(message = "UserID cannot be null")
    @Positive(message = "UserID must be positive")
    private Long userId;

    @NotNull(message = "Birth Month cannot be null")
    @Min(value = 1, message = "Birth month must be between 1 and 12")
    @Max(value = 12, message = "Birth month must be between 1 and 12")
    private Integer birthMonth;

    @NotNull(message = "Birth Day cannot be null")
    @Min(value = 1, message = "Birth day must be between 1 and 31")
    @Max(value = 31, message = "Birth day must be between 1 and 31")
    private Integer birthDay;

    @NotNull(message = "TimeZone cannot be null")
    private String timeZone;

    @NotNull(message = "nextNotificationAt cannot be null")
    private Instant nextNotificationAt;
}
