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

    FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "Co_01", "서버 처리 중 오류가 발생했습니다.", "서버 내부 에러"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Co_02", "인증되지 않은 사용자입니다.", "비정상적인 또는 만료된 토큰"),
    NOT_FOUND_DATA(HttpStatus.NOT_FOUND, "Co_03", "요청한 데이터를 찾을 수 없습니다.", "데이터가 존재하지 않음");

    private final HttpStatus status;
    private final String code;
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
