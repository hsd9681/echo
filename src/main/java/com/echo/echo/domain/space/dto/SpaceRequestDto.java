package com.echo.echo.domain.space.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SpaceRequestDto 클래스는 스페이스 생성 및 업데이트 요청에 사용되는 데이터를 정의
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceRequestDto {

    private String spaceName;
    private String isPublic;
    private byte[] thumbnail;
}
