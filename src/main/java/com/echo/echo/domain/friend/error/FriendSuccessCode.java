package com.echo.echo.domain.friend.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FriendSuccessCode implements BaseCode {
    REQUEST_REJECTED(3, "친구 요청이 거부 되었습니다.", "친구 요청이 거부 되었습니다."),
    REQUEST_ACCEPTED(4, "친구 요청이 수락되었습니다.", "친구 요청이 수락되었습니다."),
    REQUEST_SENT(5, "친구 요청이 성공적으로 전송되었습니다.", "친구 요청이 성공적으로 전송되었습니다."),
    FRIEND_DELETED(6, "친구가 성공적으로 삭제되었습니다.", "친구가 성공적으로 삭제되었습니다."),
    ;

    private final int code;
    private final String msg;
    private final String remark;

    @Override
    public CommonReason getCommonReason() {
        return CommonReason.builder()
            .status(HttpStatus.OK)
            .code(code)
            .msg(msg)
            .build();
    }
}