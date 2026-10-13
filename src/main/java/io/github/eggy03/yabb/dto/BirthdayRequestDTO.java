package io.github.eggy03.yabb.dto;

import io.github.eggy03.yabb.validation.monthday.ValidBirthMonthDay;
import io.github.eggy03.yabb.validation.zone.ValidTimeZone;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidBirthMonthDay
@ValidTimeZone
public class BirthdayRequestDTO {

    @NotNull(message = "GuildID cannot be null")
    @Positive(message = "GuildID must be positive")
    @Schema(required = true, examples = "123456789012345678")
    private Long guildId;

    @NotNull(message = "UserID cannot be null")
    @Positive(message = "UserID must be positive")
    @Schema(required = true, examples = "987654321098765432")
    private Long userId;

    @NotNull(message = "Birth Month cannot be null")
    @Schema(required = true, examples = "8")
    private Integer month;

    @NotNull(message = "Birth Day cannot be null")
    @Schema(required = true, examples = "29")
    private Integer day;

    @NotNull(message = "IANA Zone ID cannot be null")
    @Schema(required = true, examples = "Asia/Kolkata")
    private String zoneId;
}
