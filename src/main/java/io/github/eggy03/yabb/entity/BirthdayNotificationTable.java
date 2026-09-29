package io.github.eggy03.yabb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "birthday_notification_table")
@IdClass(BirthdayId.class)
public class BirthdayNotificationTable {

    @Id
    @Column(name = "guild_id")
    private long guildId;

    @Id
    @Column(name = "user_id")
    private long userId;

    @Column(name = "next_notification_at", nullable = false)
    private Instant nextNotificationAt;

}
