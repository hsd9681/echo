package com.echo.echo.common.exception.codes.success;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ChannelSuccessCode 열거형(enum)은 채널 관련 성공 메시지를 정의
 */

@Getter
@AllArgsConstructor
public enum ChannelSuccessCode implements BaseCode {
    CHANNEL_DELETE(3, "채널 삭제 완료입니다.", "채널 삭제가 성공적으로 완료되었습니다.");

    private final int code;
    private final String msg;
    private final String remark;

    @Override
    public CommonReason getCommonReason() {
        return CommonReason.builder()
                .status(HttpStatus.OK)
                .code(code)
                .msg(msg)
                .build();
    }
}
