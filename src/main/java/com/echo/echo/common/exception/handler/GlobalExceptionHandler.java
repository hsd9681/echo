package com.echo.echo.common.exception.handler;

import com.echo.echo.common.exception.CommonReason;
import com.echo.echo.common.exception.CustomException;
import com.echo.echo.common.exception.codes.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * GlobalExceptionHandler 클래스는 전역 예외 처리를 담당하며, CustomException을 처리
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Mono<CommonReason>> handleException(ServerWebExchange exchange, Exception e) {
        log.info("exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Mono.just(CommonReason.builder()
                                .code(CommonErrorCode.FAIL.getCode())
                                .msg(CommonErrorCode.FAIL.getMsg())
                                .build()));
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<Mono<CommonReason>> handleCustomException(ServerWebExchange exchange, CustomException e) {
        log.info("custom exception", e);
        return ResponseEntity.status(e.getBaseCode().getCommonReason().getStatus())
                        .body(Mono.just(e.getBaseCode().getCommonReason()));
    }
}
