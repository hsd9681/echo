package com.echo.echo.domain.space.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * SpaceRequestDto 클래스는 스페이스 생성 및 업데이트 요청에 사용되는 데이터를 정의
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceRequestDto {

    @NotBlank(message = "스페이스 이름은 필수 입력 항목입니다.")
    @Size(max = 20, message = "스페이스 이름은 20자 미만이어야 합니다.")
    private String spaceName;

    @NotBlank(message = "공개 여부는 필수 입력 항목입니다.")
    @Pattern(regexp = "^[YN]$", message = "공개 여부는 Y 또는 N이어야 합니다.")
    private String isPublic;

    private byte[] thumbnail;
}
