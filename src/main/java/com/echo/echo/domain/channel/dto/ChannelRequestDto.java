package com.echo.echo.domain.channel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * ChannelRequestDto 클래스는 채널 생성 및 업데이트 요청에 사용되는 데이터를 정의
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder

public class ChannelRequestDto {

    @NotBlank(message = "채널 이름은 필수 입력 항목입니다.")
    @Size(max = 50, message = "채널 이름은 50자 미만이어야 합니다.")
    private String channelName;

    @NotBlank(message = "채널 타입은 필수 입력 항목입니다.")
    @Pattern(regexp = "^[TV]$", message = "채널 타입은 T 또는 V이어야 합니다.")
    private String channelType;

    @NotNull(message = "채널 최대 인원수는 필수 입력 항목입니다.")
    @Min(value = 1, message = "최대 인원수는 1 이상이어야 합니다.")
    private Integer maxCapacity;

}
