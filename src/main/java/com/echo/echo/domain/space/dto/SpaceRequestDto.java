package com.echo.echo.domain.space.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceRequestDto {

    private String spaceName;
    private String isPublic;
    private byte[] thumbnail;
}
