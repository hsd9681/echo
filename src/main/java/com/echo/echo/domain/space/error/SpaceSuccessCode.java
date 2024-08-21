package com.echo.echo.domain.space.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * SpaceSuccessCode 열거형(enum)은 스페이스 관련 성공 메시지를 정의
 */

@Getter
@AllArgsConstructor
public enum SpaceSuccessCode implements BaseCode {

    SPACE_DELETE("SpS_01", "스페이스가 성공적으로 삭제되었습니다.", "스페이스 삭제가 성공적으로 완료되었습니다."),
    ;

    private final String code;
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
