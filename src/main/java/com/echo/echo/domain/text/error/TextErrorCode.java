package com.echo.echo.domain.text.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TextErrorCode implements BaseCode {

    AUTHOR_NOT_MATCHES(HttpStatus.BAD_REQUEST, 1,"요청자와 작성자의 정보가 일치하지 않습니다.", "요청자와 사용자의 정보가 일치하지 않습니다."),
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
