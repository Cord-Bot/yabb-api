package io.github.eggy03.yabb.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuildTableDTO {

    @NotNull(message = "GuildID cannot be null")
    @Positive(message = "GuildID must be positive")
    private Long guildId;

    @NotNull(message = "ChannelID cannot be null")
    @Positive(message = "ChannelID must be positive")
    private Long channelId;

}
