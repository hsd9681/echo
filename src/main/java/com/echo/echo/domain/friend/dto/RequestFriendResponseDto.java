package com.echo.echo.domain.friend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestFriendResponseDto {
    private Long fromUserId;
    private Long toUserId;
    private String status;
}
