package io.github.eggy03.yabb.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class BirthdayId implements Serializable {
    public long guildId;
    public long userId;
}
