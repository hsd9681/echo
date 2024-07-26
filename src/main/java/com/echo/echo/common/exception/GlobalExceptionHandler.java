package com.echo.echo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

/**
 * GlobalExceptionHandler 클래스는 전역 예외 처리를 담당하며, CustomException을 처리
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonReason handleCustomException(ServerWebExchange exchange, CustomException e) {
        return e.getBaseCode().getCommonReason();
    }
}
