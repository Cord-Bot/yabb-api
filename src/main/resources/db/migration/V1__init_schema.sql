create table yabb.guild_table (
     guild_id bigint not null,
     channel_id bigint not null,
    
     constraint guild_table_pk primary key (guild_id),
     constraint guild_table_unique unique (channel_id)
);

create table yabb.birthday_table (
     guild_id bigint not null,
     user_id bigint not null,
     birth_month int not null check (birth_month between 1 and 12),
     birth_day int not null check (birth_day between 1 and 31),
     timezone text not null,
     next_notification_at timestamp with time zone not null,

     constraint birthday_table_pk primary key (guild_id, user_id)
);