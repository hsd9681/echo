package com.echo.echo.domain.space.dto;

import com.echo.echo.domain.space.entity.Space;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SpaceResponseDto 클래스는 스페이스 응답 데이터
 */

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

    public SpaceResponseDto(Space space ) {
        this.id = space.getId();
        this.spaceName = space.getSpaceName();
        this.isPublic = space.getIsPublic();
        this.thumbnail = space.getThumbnail();
        this.uuid = space.getUuid();
    }
}
