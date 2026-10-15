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
@Table(name = "birthday_schedule_table", schema = "yabb")
@IdClass(BirthdayId.class)
public class BirthdaySchedule {

    @Id
    @Column(name = "guild_id")
    private Long guildId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    // must always be calculated internally
    @Column(name = "upcoming_birthday_at", nullable = false)
    private Instant upcomingBirthdayAt;
}