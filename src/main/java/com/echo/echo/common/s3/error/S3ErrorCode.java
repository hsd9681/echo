package com.echo.echo.common.s3.error;

import com.echo.echo.common.exception.BaseCode;
import com.echo.echo.common.exception.CommonReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements BaseCode {

    FILE_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, "S3_01", "파일 업로드에 실패했습니다.", "파일 업로드 중 오류 발생"),
    FILE_TYPE_INVALID(HttpStatus.BAD_REQUEST, "S3_02", "업로드할 수 있는 파일 확장자가 아닙니다.", "지원되지 않는 파일 확장자"),
    FILE_IS_NULL(HttpStatus.BAD_REQUEST, "S3_03", "파일이 유효하지 않습니다.", "유효하지 않은 파일"),
    FILE_SIZE_OVER(HttpStatus.BAD_REQUEST, "S3_04", "파일의 용량이 허용 크기를 초과했습니다.", "파일 크기 초과"),
    S3_URL_INVALID(HttpStatus.NOT_FOUND, "S3_05", "파일의 저장 경로 변환 간 오류가 발생했습니다.", "저장 경로 변환 오류")
    ;

    private final HttpStatus Status;
    private final String code;
    private final String msg;
    private final String remark;

    @Override
    public CommonReason getCommonReason() {
        return CommonReason.builder()
                .status(Status)
                .code(code)
                .msg(msg)
                .build();
    }

}
