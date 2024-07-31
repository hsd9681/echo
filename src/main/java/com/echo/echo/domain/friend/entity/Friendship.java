package com.echo.echo.domain.friend.entity;

import com.echo.echo.common.TimeStamp;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Friendship 엔티티는 친구 관계를 나타낸다
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table("friendship")
public class Friendship extends TimeStamp {

    @Id
    @Column("id")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("friend_id")
    private Long friendId;

    @Builder
    public Friendship(Long userId, Long friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }
}
