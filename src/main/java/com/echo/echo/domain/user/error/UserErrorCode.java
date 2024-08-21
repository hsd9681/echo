package com.echo.echo.domain.user.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseCode {

    INCORRECT_VERIFICATION_NUMBER(HttpStatus.BAD_REQUEST, "Us_01", "인증번호가 올바르지 않습니다.", "잘못된 인증번호입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "Us_02", "해당 유저를 찾을 수 없습니다.", "해당 이메일을 찾을 수 없습니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "Us_03", "이미 존재하는 이메일입니다.", "이미 등록된 이메일입니다."),
    ALREADY_ACCOUNT_ACTIVATED(HttpStatus.BAD_REQUEST, "Us_04", "이미 인증된 계정입니다.", "이미 활성화된 계정입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "Us_05", "비밀번호가 일치하지 않습니다.", "비밀번호가 올바르지 않습니다."),
    VERIFICATION_ID_NOT_MATCH(HttpStatus.BAD_REQUEST, "Us_06", "해당 인증번호를 찾을 수 없습니다.", "인증 코드 UUID가 일치하지 않습니다."),
    VERIFY_BEFORE(HttpStatus.BAD_REQUEST, "Us_07", "인증되지 않은 이메일입니다. 인증 후 다시 시도해주세요.", "아직 인증이 완료되지 않았습니다."),
    VERIFICATION_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "Us_08", "이미 인증된 이메일입니다.", "이미 인증이 완료된 이메일입니다."),
    EMAIL_FORMAT_INVALID(HttpStatus.BAD_REQUEST, "Us_09", "이메일 형식이 올바르지 않습니다.", "잘못된 이메일 형식입니다."),
    PASSWORD_FORMAT_INVALID(HttpStatus.BAD_REQUEST, "Us_10", "비밀번호 형식이 올바르지 않습니다.", "비밀번호는 대소문자, 숫자, 특수문자(~!@#$%^&*)를 포함하여 8자 이상이어야 합니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
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
