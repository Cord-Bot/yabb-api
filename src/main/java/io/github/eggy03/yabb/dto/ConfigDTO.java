package io.github.eggy03.yabb.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDTO {

    @NotNull(message = "GuildID cannot be null")
    @Positive(message = "GuildID must be positive")
    @Schema(required = true, description = "Discord guild/server ID", examples = {"123456789012345678"})
    private Long guildId;

    @NotNull(message = "ChannelID cannot be null")
    @Positive(message = "ChannelID must be positive")
    @Schema(required = true, description = "Discord channel ID where birthday notifications are sent", examples = {"987654321098765432"})
    private Long channelId;

    @Positive(message = "RoleID must be positive")
    @Schema(description = "Discord role ID to ping when birthday notifications are sent", examples = {"1234567891051454842"})
    private Long roleId;
}