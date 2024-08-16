package com.echo.echo.domain.channel.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ChannelErrorCode 열거형(enum)은 채널 관련 에러 코드를 정의
 */
@Getter
@AllArgsConstructor
public enum ChannelErrorCode implements BaseCode {

    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "채널 데이터를 찾을 수 없습니다.", "채널 데이터가 존재하지 않습니다."),
    CHANNEL_FULL(HttpStatus.FORBIDDEN, 403, "채널에 인원이 가득 차서 입장할 수 없습니다.", "채널 최대 인원을 초과하였습니다."),
    CHANNEL_EMPTY(HttpStatus.FORBIDDEN, 403, "채널에 인원이 존재하지 않습니다.", "채널 인원이 0명입니다."),
    INVALID_CHANNEL_NAME(HttpStatus.BAD_REQUEST, 5003, "채널 이름은 50자 미만이어야 합니다.", "채널 이름이 유효하지 않습니다."),
    INVALID_CHANNEL_TYPE(HttpStatus.BAD_REQUEST, 5004, "채널 타입은 T 또는 V이어야 합니다.", "채널 타입이 유효하지 않습니다.");

    private final HttpStatus status;
    private final int code;
    private final String msg;
    private final String remark;

    @Override
    public CommonReason getCommonReason() {
        return CommonReason.builder()
            .status(status)
            .code(code)
            .msg(msg)
            .build();
    }
}
