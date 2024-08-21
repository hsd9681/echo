package com.echo.echo.domain.auth.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseCode {

    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "Au_01", "비밀번호가 일치하지 않습니다.", "비밀번호가 일치하지 않음"),
    NOT_ACTIVATED_ACCOUNT(HttpStatus.BAD_REQUEST, "Au_02", "메일 인증이 안된 계정입니다. 메일 인증 완료 후 다시 시도해주세요.", "계정 활성화 필요"),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Au_03", "자격증명에 실패하였습니다.", "리프레시 토큰을 찾을 수 없음"),
    ;

    private final HttpStatus Status;
    private final String code;
    private final String msg;
    private final String remark;

    @Override
    public CommonReason getCommonReason() {
        return CommonReason.builder()
            .status(Status)
            .code(code)
            .msg(msg)
            .build();
    }

}
