package com.echo.echo.domain.friend.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * RequestFriendResponseDto는 친구 요청에 대한 응답 데이터를 나타낸다
 */


@Getter
@Builder
public class RequestFriendResponseDto {

    private Long fromUserId;
    private Long toUserId;
    private String status;

}
