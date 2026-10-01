package io.github.eggy03.yabb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BirthdayResponseDTO {

    @Schema(examples = {"123456789012345678"})
    private Long guildId;

    @Schema(examples = {"987654321098765432"})
    private Long userId;

    @Schema(examples = {"8"})
    private Integer month;

    @Schema(examples = {"29"})
    private Integer day;

    @Schema(description = "IANA time zone ID", examples = {"Asia/Kolkata"})
    private String zoneId;

    @Schema(description = "Next scheduled birthday notification time in UTC", examples = {"2027-08-28T18:30:00Z"})
    private Instant nextBirthdayAt;
}