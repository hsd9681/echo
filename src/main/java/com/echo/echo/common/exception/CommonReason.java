package com.echo.echo.common.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * CommonReason 클래스는 에러 코드, 메시지, 설명 등을 포함하는 공통 이유 객체를 정의
 */

@Getter
public class CommonReason {

    private final HttpStatus status;
    private final int code;
    private final String msg;

    @Builder
    public CommonReason(HttpStatus status, int code, String msg) {
        this.status = status;
        this.code = code;
        this.msg = msg;
    }

}
