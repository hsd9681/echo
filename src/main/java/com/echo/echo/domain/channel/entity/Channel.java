package com.echo.echo.domain.channel.entity;

import com.echo.echo.domain.space.entity.Space;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Channel 엔티티는 채널 테이블의 데이터를 매핑
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "channel")
public class Channel {

    @Id
    private Long id;
    private Long spaceId;
    private String channelName;
    private String channelType;

    @Transient
    private Space space;

    @Builder
    public Channel(Long id, Long spaceId, String channelName, String channelType, Space space) {
        this.id = id;
        this.spaceId = spaceId;
        this.channelName = channelName;
        this.channelType = channelType;
        this.space = space;
    }

    public enum Type {
        T, V
    }
}
