package com.echo.echo.domain.channel.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ChannelResponseDto 클래스는 채널 응답 데이터를 정의
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ChannelResponseDto {

    private Long id;
    private String channelName;
    private String channelType;
    private Integer maxCapacity;
    private Integer currentMemberCount;
    private boolean push;
    private String lastReadMessageId;
}