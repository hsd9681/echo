package com.echo.echo.domain.friend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendshipResponseDto {
    private Long userId;
    private Long friendId;
}
