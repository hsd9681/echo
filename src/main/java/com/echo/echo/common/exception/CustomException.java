package com.echo.echo.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * CustomException 클래스는 사용자 정의 예외를 정의하며, BaseCode를 사용하여 에러 정보를 줌
 */

@Getter
@Slf4j
public class CustomException extends RuntimeException {
    private final BaseCode baseCode;

    public CustomException(BaseCode baseCode) {
        super(baseCode.getRemark());
        this.baseCode = baseCode;
        log.info("ExceptionMethod: {}", getExceptionMethod());
        log.info("ErrorCode: {}, ErrorMsg: {}", baseCode.getCommonReason().getCode(), baseCode.getCommonReason().getMsg());
    }

    public String getExceptionMethod() {
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        return className + "." + methodName;
    }
}
