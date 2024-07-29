package com.echo.echo.common.exception.codes.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * SpaceErrorCode 열거형(enum)은 스페이스 관련 에러 코드를 정의
 */
@Getter
@AllArgsConstructor
public enum SpaceErrorCode implements BaseCode {

    SPACE_ENTRY_FAILURE(HttpStatus.BAD_REQUEST, 5000, "입장 실패: 코드가 유효하지 않습니다.", "유효하지 않은 입장 코드입니다."),
    SPACE_ALREADY_JOINED(HttpStatus.BAD_REQUEST, 409, "입장 실패: 이미 입장한 사용자입니다.", "이미 입장한 사용자입니다."),
    SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "스페이스 데이터를 찾을 수 없습니다.", "스페이스 데이터가 존재하지 않습니다.");

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
