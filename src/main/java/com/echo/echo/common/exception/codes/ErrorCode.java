package com.echo.echo.common.exception.codes;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ErrorCode 열거형(enum)은 다양한 에러 코드와 메시지를 정의
 */

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {
    FAIL(HttpStatus.INTERNAL_SERVER_ERROR, -1, "실패했습니다.", "서버 에러"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "로그인이 안되어있거나 만료된 사용자입니다.", "비정상적인 토큰입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "권한이 없습니다.", "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND,10, "데이터를 찾을 수 없습니다.", "데이터가 존재하지 않습니다."),
    ENTRY_FAILURE(HttpStatus.BAD_REQUEST, 5000,"입장 실패: 코드가 유효하지 않습니다.", "유효하지 않은 입장 코드입니다."),
    ALREADY_JOINED(HttpStatus.BAD_REQUEST, 409, "입장 실패: 이미 입장한 사용자입니다.", "이미 입장한 사용자입니다.");

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
