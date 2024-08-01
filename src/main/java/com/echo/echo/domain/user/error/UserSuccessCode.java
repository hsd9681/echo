package com.echo.echo.domain.user.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserSuccessCode implements BaseCode {

    PASSWORD_CHANGE_SUCCESS(HttpStatus.OK, 200, "비밀번호가 성공적으로 변경되었습니다.", "비밀번호 변경 성공");

    private final HttpStatus httpStatus;
    private final int code;
    private final String msg;
    private final String remark;

    @Override
    public CommonReason getCommonReason() {
        return CommonReason.builder()
            .status(httpStatus)
            .code(code)
            .msg(msg)
            .build();
    }
}
