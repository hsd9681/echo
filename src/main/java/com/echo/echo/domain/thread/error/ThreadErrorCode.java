package com.echo.echo.domain.thread.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ThreadErrorCode implements BaseCode {

    NOT_FOUND_THREAD(HttpStatus.BAD_REQUEST, 8000, "해당하는 스레드를 찾을 수 없습니다", "해당하는 스레드 id에 대한 데이터를 찾을 수 없습니다."),
    ALREADY_EXISTS_THREAD(HttpStatus.BAD_REQUEST, 8001, "해당하는 채팅 메시지에 이미 스레드가 존재합니다.", "해당하는 채팅 메시지에 이미 스레드가 존재합니다."),
    NOT_THREAD_MESSAGE_AUTHOR(HttpStatus.BAD_REQUEST, 8002, "해당하는 스레드 메시지의 작성자가 아닙니다.", "해당하는 스레드 메시지의 작성자가 아닙니다.")
    ;

    private final HttpStatus status;
    private final int code;
    private final String msg;
    private final String remark;

    @Override
    public CommonReason getCommonReason() {
        return CommonReason.builder()
                .status(status)
                .code(code)
                .msg(msg)
                .build();
    }
}
