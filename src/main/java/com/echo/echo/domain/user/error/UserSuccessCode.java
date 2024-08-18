package com.echo.echo.domain.user.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserSuccessCode implements BaseCode {

    PASSWORD_CHANGE_SUCCESS(HttpStatus.OK, 200, "비밀번호가 성공적으로 변경되었습니다.", "비밀번호 변경 성공"),
    FOUND_USER(HttpStatus.OK, 201, "해당 이메일은 가입되어 있습니다.", "해당 이메일 데이터가 있습니다."),
    NOT_FOUND_USER(HttpStatus.OK, 202, "해당 이메일은 가입되어 있지 않습니다.", "해당 이메일 데이터가 없습니다."),
    VERIFICATION_SUCCESS(HttpStatus.OK, 203, "인증 성공되었습니다.", "인증 성공되었습니다."),
    VERIFICATION_CODE_SENT(HttpStatus.OK, 204, "해당 이메일로 인증번호가 전송되었습니다", "인증번호 발급 성공"),
    ;

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
