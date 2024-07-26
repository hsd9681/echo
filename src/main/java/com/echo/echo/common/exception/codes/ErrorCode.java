package com.echo.echo.common.exception.codes;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ErrorCode 열거형(enum)은 다양한 에러 코드와 메시지를 정의
 */

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {
    FAIL(-1, "실패했습니다.", "서버 에러"),
    NOT_FOUND(404, "데이터를 찾을 수 없습니다.", "데이터가 존재하지 않습니다."),
    ENTRY_FAILURE(401, "입장 실패: 코드가 유효하지 않습니다.", "유효하지 않은 입장 코드입니다."),
    ALREADY_JOINED(409, "입장 실패: 이미 입장한 사용자입니다.", "이미 입장한 사용자입니다.");

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
