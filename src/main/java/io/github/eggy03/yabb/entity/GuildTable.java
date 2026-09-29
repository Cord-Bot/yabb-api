package io.github.eggy03.yabb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "guild_table")
public class GuildTable {

    @Id
    @Column(name = "guild_id")
    private long guildId;

    @Column(name = "channel_id", unique = true, nullable = false)
    private long channelId;
}