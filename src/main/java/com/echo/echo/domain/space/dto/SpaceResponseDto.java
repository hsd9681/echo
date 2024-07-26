package com.echo.echo.domain.space.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SpaceResponseDto {
    private Long id;
    private String spaceName;
    private String isPublic;
    private byte[] thumbnail;
    private String uuid;
    private String message;
}
