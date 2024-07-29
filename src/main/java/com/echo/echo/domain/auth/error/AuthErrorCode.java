package com.echo.echo.domain.auth.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseCode {
    PASSWORD_NOT_MATCH(HttpStatus.FORBIDDEN, 50,"비밀번호가 일치하지 않습니다.", "비밀번호가 일치하지 않습니다."),
    NOT_ACTIVATED_ACCOUNT(HttpStatus.UNAUTHORIZED, 51, "메일 인증이 안된 계정입니다. 메일 인증 완료 후 다시 시도해주세요.", "계정 활성화가 필요합니다."),
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
