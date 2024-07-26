package com.echo.echo.common.exception;

import lombok.Builder;
import lombok.Getter;

/**
 * CommonReason 클래스는 에러 코드, 메시지, 설명 등을 포함하는 공통 이유 객체를 정의
 */

@Getter
@Builder
public class CommonReason {
    private final int code;
    private final String msg;
    private final String remark;
    private final String name;
}
