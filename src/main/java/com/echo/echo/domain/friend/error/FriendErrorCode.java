package com.echo.echo.domain.friend.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * FriendErrorCode는 친구 관련 오류 코드를 정의
 */

@Getter
@AllArgsConstructor
public enum FriendErrorCode implements BaseCode {

    REQUEST_ALREADY_SENT(HttpStatus.BAD_REQUEST, "Fr_01", "이미 친구 요청을 보냈습니다.", "이미 친구 요청을 보낸 상태입니다."),
    ALREADY_FRIENDS(HttpStatus.BAD_REQUEST, "Fr_02", "이미 친구입니다.", "이미 친구 관계입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Fr_03", "사용자를 찾을 수 없습니다.", "해당 사용자가 존재하지 않습니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "Fr_04", "잘못된 사용자 ID입니다.", "유효하지 않은 사용자 ID입니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "Fr_05", "친구 요청을 찾을 수 없습니다.", "해당 친구 요청이 존재하지 않습니다."),
    FRIEND_REQUEST_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "Fr_06", "이미 처리된 친구 요청입니다.", "이미 처리된 친구 요청입니다."),
    NO_FRIEND_REQUESTS(HttpStatus.NOT_FOUND, "Fr_07", "친구 요청이 현재 없습니다.", "현재 친구 요청이 없습니다."),
    NO_FRIENDS_FOUND(HttpStatus.NOT_FOUND, "Fr_08", "친구가 없습니다.", "친구 목록이 비어 있습니다."),
    NOT_FRIENDS(HttpStatus.BAD_REQUEST, "Fr_09", "이 사람과 친구가 아닙니다.", "해당 사용자와 친구 관계가 아닙니다."),
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
