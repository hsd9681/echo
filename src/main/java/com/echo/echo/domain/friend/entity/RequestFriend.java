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
 * RequestFriend 엔티티는 친구 요청을 나타낸다
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table("request_friend")
public class RequestFriend extends TimeStamp {

    @Id
    @Column("id")
    private Long id;

    @Column("from_user_id")
    private Long fromUserId;

    @Column("to_user_id")
    private Long toUserId;

    @Column("status")
    private Status status;

    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }

    @Builder
    public RequestFriend(Long fromUserId, Long toUserId, Status status) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.status = status;
    }

    public RequestFriend changeStatus(Status newStatus) {
        return RequestFriend.builder()
            .fromUserId(this.fromUserId)
            .toUserId(this.toUserId)
            .status(newStatus)
            .build();
    }
}
