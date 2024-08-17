package com.echo.echo.common.exception.codes;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * CommonErrorCode 열거형(enum)은 공통적으로 사용할 에러 코드를 정의
 */
@Getter
@AllArgsConstructor
public enum CommonErrorCode implements BaseCode {

    FAIL(HttpStatus.INTERNAL_SERVER_ERROR, -1, "실패했습니다.", "서버 에러"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "로그인이 안되어있거나 만료된 사용자입니다.", "비정상적인 토큰입니다."),
    NOT_FOUND_DATA(HttpStatus.NOT_FOUND, 402, "데이터를 찾을 수 없습니다.", "데이터를 찾을 수 없습니다.");
    ;

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
