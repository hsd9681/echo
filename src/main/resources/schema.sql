create table if not exists user
(
    id                bigint primary key auto_increment,
    nickname          varchar(50),
    email             varchar(50),
    password          varchar(255),
    intro             varchar(100),
    status            int(1),
    kakao_id bigint(20)
);

create table if not exists space
(
    id          bigint auto_increment primary key,
    space_name  varchar(20)            not null,
    is_public   varchar(1) default 'n' not null,
    thumbnail   blob,
    uuid        varchar(36)            not null,
    created_at  timestamp              not null,
    modified_at timestamp              not null
);

create table if not exists channel
(
    id                     bigint primary key auto_increment,
    channel_name           varchar(50),
    channel_type           varchar(1),
    space_id               bigint not null,
    max_capacity           int not null,
    current_member_count   int not null,
    version                bigint not null,
    foreign key (space_id) references space (id)
);

create table if not exists space_member
(
    user_id  bigint,
    space_id bigint,
    primary key (user_id, space_id),
    foreign key (user_id) references user (id),
    foreign key (space_id) references space (id)
);

create table if not exists text
(
    id          bigint primary key auto_increment,
    contents    text(1000) not null,
    username varchar(50) not null,
    user_id     bigint     not null,
    channel_id  bigint     not null,
    created_at  timestamp  not null,
    modified_at timestamp  not null,
    foreign key (user_id) references user (id),
    foreign key (channel_id) references channel (id)
);

CREATE TABLE IF NOT EXISTS friendship
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT    NOT NULL,
    friend_id   BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (friend_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS request_friend
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    from_user_id BIGINT      NOT NULL,
    to_user_id   BIGINT      NOT NULL,
    status       VARCHAR(10) NOT NULL,
    created_at   TIMESTAMP   NOT NULL,
    modified_at  TIMESTAMP   NOT NULL,
    FOREIGN KEY (from_user_id) REFERENCES user (id),
    FOREIGN KEY (to_user_id) REFERENCES user (id)
);

INSERT INTO user (nickname, email, password, intro, status)
SELECT '정현경', 'gusrud@test.com', '$2a$10$6UXZchkxO93nMUKrt.kXTeHx6o1/6Dij8eDfp5UBMWFJQAT2xG.GW', '', 0
WHERE NOT EXISTS (SELECT 1 FROM user WHERE email = 'gusrud@test.com');

INSERT INTO user (nickname, email, password, intro, status)
SELECT '홍성도', 'tjdeh@test.com', '$2a$10$6UXZchkxO93nMUKrt.kXTeHx6o1/6Dij8eDfp5UBMWFJQAT2xG.GW', '', 0
WHERE NOT EXISTS (SELECT 1 FROM user WHERE email = 'tjdeh@test.com');

INSERT INTO user (nickname, email, password, intro, status)
SELECT '김기석', 'rltjr@test.com', '$2a$10$6UXZchkxO93nMUKrt.kXTeHx6o1/6Dij8eDfp5UBMWFJQAT2xG.GW', '', 0
WHERE NOT EXISTS (SELECT 1 FROM user WHERE email = 'rltjr@test.com');

INSERT INTO user (nickname, email, password, intro, status)
SELECT '이유환', 'dbghks@test.com', '$2a$10$6UXZchkxO93nMUKrt.kXTeHx6o1/6Dij8eDfp5UBMWFJQAT2xG.GW', '', 0
WHERE NOT EXISTS (SELECT 1 FROM user WHERE email = 'dbghks@test.com');

INSERT INTO user (nickname, email, password, intro, status)
SELECT '최현진', 'guswls@test.com', '$2a$10$6UXZchkxO93nMUKrt.kXTeHx6o1/6Dij8eDfp5UBMWFJQAT2xG.GW', '', 0
WHERE NOT EXISTS (SELECT 1 FROM user WHERE email = 'guswls@test.com');
