package com.echo.echo.domain.channel.entity;

import com.echo.echo.domain.space.entity.Space;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Channel {
    @Id
    private Long id;
    private Long spaceId;
    private String channelName;
    private String channelType;
    @Transient
    private Space space;

    @Builder
    public Channel(Space space, String channelName, Type channelType) {
        this.spaceId = space.getId();
        this.channelName = channelName;
        this.channelType = channelType.name();
        this.space = space;
    }

    public enum Type {
        T, V
    }
}
