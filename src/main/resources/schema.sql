create table if not exists user (
    id bigint primary key auto_increment,
    email varchar(50),
    password varchar(255),
    intro varchar(100),
    status int(1),
    verification_code int(6)
);

create table if not exists space (
    id bigint auto_increment primary key,
    space_name varchar(20) not null,
    is_public varchar(1) default 'n' not null,
    thumbnail blob,
    uuid varchar(36) not null,
    created_at timestamp not null,
    modified_at timestamp not null
);

create table if not exists channel (
    id bigint primary key auto_increment,
    channel_name varchar(50),
    channel_type varchar(1),
    space_id bigint not null,
    foreign key(space_id) references space(id)
);

create table if not exists space_member (
    user_id bigint,
    space_id bigint,
    primary key(user_id, space_id),
    foreign key(user_id) references user(id),
    foreign key(space_id) references space(id)
);

create table if not exists text (
    id bigint primary key auto_increment,
    contents text(1000) not null,
    user_id bigint not null,
    channel_id bigint not null,
    created_at timestamp not null,
    modified_at timestamp not null,
    foreign key(user_id) references user(id),
    foreign key(channel_id) references channel(id)
);


