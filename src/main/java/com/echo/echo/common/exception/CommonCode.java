package com.echo.echo.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * CommonCode 열거형(enum)은 공통적으로 사용할 성공 메시지를 정의
 */

@Getter
@AllArgsConstructor
public enum CommonCode implements BaseCode {
    SUCCESS(1, "성공하였습니다.", "성공하였습니다.");

    private final int code;
    private final String msg;
    private final String remark;

    @Override
    public CommonReason getCommonReason() {
        return CommonReason.builder()
                .code(code)
                .msg(msg)
                .remark(remark)
                .name(name())
                .build();
    }
}
