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

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "birthday_table")
@IdClass(BirthdayId.class)
public class BirthdayTable {

    @Id
    @Column(name = "guild_id")
    private long guildId;

    @Id
    @Column(name = "user_id")
    private long userId;

    @Column(name = "birth_month", nullable = false)
    private int birthMonth;

    @Column(name = "birth_day", nullable = false)
    private int birthDay;

    @Column(name = "timezone", nullable = false)
    private String timeZone;
}