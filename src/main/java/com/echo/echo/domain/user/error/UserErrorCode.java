package com.echo.echo.domain.user.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseCode {

    INCORRECT_VERIFICATION_NUMBER(HttpStatus.BAD_REQUEST, 100, "인증번호가 올바르지 않습니다.", "인증번호가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, 101, "해당하는 유저를 찾을 수 없습니다.", "해당하는 이메일을 찾을 수 없습니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, 102, "이미 존재하는 이메일입니다.", "이미 존재하는 이메일입니다."),
    ALREADY_ACCOUNT_ACTIVATED(HttpStatus.BAD_REQUEST, 103, "이미 인증 완료된 계정입니다.", "이미 활성화 완료된 계정입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, 104, "패스워드가 일치하지 않습니다.", "패스워드가 일치하지 않습니다."),
    EMAIL_FORMAT_INVALID(HttpStatus.BAD_REQUEST, 105, "이메일 형식이 올바르지 않습니다.", "이메일 형식이 올바르지 않습니다."),
    PASSWORD_FORMAT_INVALID(HttpStatus.BAD_REQUEST, 106, "비밀번호 형식이 올바르지 않습니다.", "비밀번호는 대소문자, 숫자, 특수문자(~!@#$%^&*)를 포함하여 8자 이상이어야 합니다."),
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
