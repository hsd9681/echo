package com.echo.echo.common.exception.handler;

import com.echo.echo.common.exception.CommonReason;
import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.user.error.UserErrorCode;
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

    @ExceptionHandler(Exception.class)
    protected Mono<ResponseEntity<CommonReason>> handleGeneralException(Exception e) {
        log.error("Exception: {}", e.getMessage());
        CommonReason reason = CommonReason.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .code(-1)
            .msg("서버에서 오류가 발생했습니다.")
            .build();
        return Mono.just(ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(reason));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    protected Mono<ResponseEntity<CommonReason>> handleValidationException(WebExchangeBindException e) {
        String defaultMessage = e.getAllErrors().get(0).getDefaultMessage();
        UserErrorCode errorCode;

        if (defaultMessage.contains("이메일 형식이 올바르지 않습니다.")) {
            errorCode = UserErrorCode.EMAIL_FORMAT_INVALID;
        } else if (defaultMessage.contains("비밀번호는 대소문자, 숫자, 특수문자(~!@#$%^&*)를 포함하여 8자 이상이어야 합니다.")) {
            errorCode = UserErrorCode.PASSWORD_FORMAT_INVALID;
        } else {
            errorCode = UserErrorCode.USER_NOT_FOUND;
        }

        CommonReason reason = errorCode.getCommonReason();
        return Mono.just(ResponseEntity.status(reason.getStatus()).body(reason));
    }
}
