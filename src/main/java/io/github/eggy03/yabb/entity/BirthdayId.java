package io.github.eggy03.yabb.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BirthdayId implements Serializable {
    public long guildId;
    public long userId;
}
