create table yabb.config_table
(
    guild_id   bigint not null,
    channel_id bigint not null,
    role_id    bigint,

    constraint config_table_pk primary key (guild_id),
    constraint config_table_unique_channel_id unique (channel_id),
    constraint config_table_unique_role_id unique (role_id)
);

create table yabb.birthday_table
(
    guild_id    bigint not null,
    user_id     bigint not null,
    birth_month int    not null check (birth_month between 1 and 12),
    birth_day   int    not null check (birth_day between 1 and 31),
    zone_id     text   not null,

    constraint birthday_table_pk primary key (guild_id, user_id)
);

create table yabb.birthday_schedule_table
(
    guild_id             bigint                   not null,
    user_id              bigint                   not null,
    upcoming_birthday_at timestamp with time zone not null,

    primary key (guild_id, user_id),

    foreign key (guild_id, user_id)
        references yabb.birthday_table (guild_id, user_id)
        on delete cascade
);

create index idx_upcoming_birthday_at
    on yabb.birthday_schedule_table (upcoming_birthday_at);