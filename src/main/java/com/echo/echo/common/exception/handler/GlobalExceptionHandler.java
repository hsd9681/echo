package com.echo.echo.common.exception.handler;

import com.echo.echo.common.exception.CommonReason;
import com.echo.echo.common.exception.CustomException;
import com.echo.echo.common.exception.codes.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

/**
 * GlobalExceptionHandler 클래스는 전역 예외 처리를 담당하며, CustomException을 처리
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected Mono<ResponseEntity<CommonReason>> handleCustomException(CustomException e) {
        log.error("CustomException: {}", e.getMessage());
        CommonReason reason = e.getBaseCode().getCommonReason();
        return Mono.just(ResponseEntity
            .status(reason.getStatus())
            .body(reason));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    protected Mono<ResponseEntity<CommonReason>> handleValidationException(WebExchangeBindException e) {
        String defaultMessage = e.getAllErrors().get(0).getDefaultMessage();
        CommonReason reason = CommonReason.builder()
            .status(HttpStatus.BAD_REQUEST)
            .code("400")
            .msg(defaultMessage)
            .build();
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason));
    }

    @ExceptionHandler(Exception.class)
    protected Mono<ResponseEntity<CommonReason>> handleGeneralException(Exception e) {
        log.error("Exception: {}", e.getMessage());
        CommonReason reason = CommonReason.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .code(CommonErrorCode.FAIL.getCode())
            .msg(CommonErrorCode.FAIL.getMsg())
            .build();
        return Mono.just(ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(reason));
    }

}
