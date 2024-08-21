package com.echo.echo.domain.space.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * SpaceErrorCode 열거형(enum)은 스페이스 관련 에러 코드를 정의
 */
@Getter
@AllArgsConstructor
public enum SpaceErrorCode implements BaseCode {

    SPACE_ENTRY_FAILURE(HttpStatus.BAD_REQUEST, "Sp_01", "입장 실패: 유효하지 않은 코드입니다.", "입장 코드가 유효하지 않습니다."),
    SPACE_ALREADY_JOINED(HttpStatus.BAD_REQUEST, "Sp_02", "입장 실패: 이미 입장한 사용자입니다.", "사용자가 이미 입장한 상태입니다."),
    SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "Sp_03", "스페이스를 찾을 수 없습니다.", "해당 스페이스 데이터가 존재하지 않습니다."),
    INVALID_SPACE_NAME(HttpStatus.BAD_REQUEST, "Sp_04", "스페이스 이름은 20자 이하여야 합니다.", "유효하지 않은 스페이스 이름입니다."),
    INVALID_IS_PUBLIC(HttpStatus.BAD_REQUEST, "Sp_05", "공개 여부는 'Y' 또는 'N'이어야 합니다.", "유효하지 않은 공개 여부 값입니다."),
    NO_SPACES_JOINED(HttpStatus.NOT_FOUND, "Sp_06", "가입된 스페이스가 없습니다.", "사용자가 가입한 스페이스가 없습니다."),
    NOT_SPACE_MEMBER(HttpStatus.BAD_REQUEST, "Sp_07", "스페이스 멤버가 아닙니다.", "해당 스페이스의 멤버로 등록되어 있지 않습니다."),

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
