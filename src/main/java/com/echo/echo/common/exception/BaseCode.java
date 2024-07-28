package com.echo.echo.common.exception;

/**
 * BaseCode 인터페이스는 공통적으로 사용할 에러 코드와 메시지 형식을 정의
 */

public interface BaseCode {
    public String getRemark();
    public CommonReason getCommonReason();
}
