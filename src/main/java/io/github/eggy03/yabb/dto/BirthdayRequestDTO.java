package io.github.eggy03.yabb.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BirthdayRequestDTO {

    @NotNull(message = "GuildID cannot be null")
    @Positive(message = "GuildID must be positive")
    @Schema(examples = "123456789012345678")
    private Long guildId;

    @NotNull(message = "UserID cannot be null")
    @Positive(message = "UserID must be positive")
    @Schema(examples = "987654321098765432")
    private Long userId;

    @NotNull(message = "Birth Month cannot be null")
    @Min(value = 1, message = "Birth Month must be between 1 and 12")
    @Max(value = 12, message = "Birth Month must be between 1 and 12")
    @Schema(examples = "8")
    private Integer month;

    @NotNull(message = "Birth Day cannot be null")
    @Min(value = 1, message = "Birth Day must be between 1 and 31")
    @Max(value = 31, message = "Birth Day must be between 1 and 31")
    @Schema(examples = "29")
    private Integer day;

    @NotNull(message = "IANA Zone ID cannot be null")
    @Schema(examples = "Asia/Kolkata")
    private String zoneId;
}
