package com.echo.echo.common.exception.codes;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * CommonCode 열거형(enum)은 공통적으로 사용할 성공 메시지를 정의
 */

@Getter
@AllArgsConstructor
public enum CommonCode implements BaseCode {
    SUCCESS(1, "성공하였습니다.", "성공하였습니다."),
    ENTRY_SUCCESS(2, "입장 성공입니다.", "입장이 성공적으로 완료되었습니다."),
    DELETE_SUCCESS(3, "삭제 완료입니다.", "삭제가 성공적으로 완료되었습니다.");

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
