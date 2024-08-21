package com.echo.echo.domain.thread.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ThreadErrorCode implements BaseCode {

    NOT_FOUND_THREAD(HttpStatus.BAD_REQUEST, "Th_01", "해당 스레드를 찾을 수 없습니다.", "해당 스레드 ID에 대한 데이터를 찾을 수 없습니다."),
    ALREADY_EXISTS_THREAD(HttpStatus.BAD_REQUEST, "Th_02", "해당 채팅 메시지에 이미 스레드가 존재합니다.", "해당 채팅 메시지에 이미 스레드가 존재합니다."),
    NOT_THREAD_MESSAGE_AUTHOR(HttpStatus.BAD_REQUEST, "Th_03", "해당 스레드 메시지의 작성자가 아닙니다.", "해당 스레드 메시지의 작성자가 아닙니다.")
    ;

    private final HttpStatus status;
    private final String code;
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
