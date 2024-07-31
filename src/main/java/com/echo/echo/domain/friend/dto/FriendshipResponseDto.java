package com.echo.echo.domain.friend.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * FriendshipResponseDto는 친구 관계에 대한 응답 데이터를 나타낸다
 */

@Getter
@Builder
public class FriendshipResponseDto {
    private Long userId;
    private Long friendId;
}
