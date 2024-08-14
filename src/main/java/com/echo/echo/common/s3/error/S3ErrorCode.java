package com.echo.echo.common.s3.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements BaseCode {
    FILE_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, 90,"파일 업로드에 실패했습니다.", "파일 업로드에 실패했습니다."),
    FILE_TYPE_INVALID(HttpStatus.BAD_REQUEST, 91, "업로드할 수 있는 파일 확장자가 아닙니다.", "업드할 수 있는 파일 확장자가 아닙니다."),
    FILE_IS_NULL(HttpStatus.BAD_REQUEST, 92, "파일이 유효하지 않습니다.", "파일이 유효하지 않습니다."),
    FILE_SIZE_OVER(HttpStatus.BAD_REQUEST, 93, "파일의 용량이 허용크기를 초과했습니다.", "파일의 용량이 허용크기를 초과했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String msg;
    private final String remark;

    @Override
    public CommonReason getCommonReason() {
        return CommonReason.builder()
                .status(httpStatus)
                .code(code)
                .msg(msg)
                .build();
    }
}
