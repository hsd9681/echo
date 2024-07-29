package com.echo.echo.domain.channel.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ChannelRequestDto 클래스는 채널 생성 및 업데이트 요청에 사용되는 데이터를 정의
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder

public class ChannelRequestDto {

    private String channelName;
    private String channelType;

}