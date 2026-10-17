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
@Table(name = "config_table", schema = "yabb")
public class Config {

    @Id
    @Column(name = "guild_id")
    private Long guildId;

    @Column(name = "channel_id", unique = true, nullable = false)
    private Long channelId;

    @Column(name = "role_id", unique = true)
    private Long roleId;
}