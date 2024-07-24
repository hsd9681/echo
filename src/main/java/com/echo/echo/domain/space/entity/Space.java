package com.echo.echo.domain.space.entity;

import com.echo.echo.common.TimeStamp;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "space")
public class Space extends TimeStamp {

    @Id
    private Long id;
    private String spaceName;
    private String isPublic;
    private byte[] thumnail;
    private String uuid;

    @Builder
    public Space(String spaceName, String isPublic, byte[] thumnail) {
        this.spaceName = spaceName;
        this.isPublic = isPublic;
        this.thumnail = thumnail;
        this.uuid = UUID.randomUUID().toString();
    }
}