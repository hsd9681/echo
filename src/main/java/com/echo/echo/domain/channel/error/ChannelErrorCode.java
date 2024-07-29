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

    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "채널 데이터를 찾을 수 없습니다.", "채널 데이터가 존재하지 않습니다.");

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
