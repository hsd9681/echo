package com.echo.echo.domain.space.entity;

import com.echo.echo.common.TimeStamp;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * Space 엔티티는 스페이스 테이블의 데이터를 매핑
 */

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "space")
public class Space extends TimeStamp {

    @Id
    private Long id;

    private String spaceName;
    private String isPublic;
    private byte[] thumbnail;
    private String uuid;

    @Builder
    public Space(Long id, String spaceName, String isPublic, byte[] thumbnail, String uuid) {
        this.id = id;
        this.spaceName = spaceName;
        this.isPublic = isPublic;
        this.thumbnail = thumbnail;
        this.uuid = uuid != null ? uuid : UUID.randomUUID().toString();
    }

    public Space update(String spaceName, String isPublic, byte[] thumbnail) {
        return Space.builder()
            .id(this.id)
            .spaceName(spaceName)
            .isPublic(isPublic)
            .thumbnail(thumbnail)
            .uuid(this.uuid)
            .build();
    }

}
