package com.echo.echo.domain.channel.entity;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.channel.error.ChannelErrorCode;
import com.echo.echo.domain.space.entity.Space;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
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
    private Integer maxCapacity;
    private Integer currentMemberCount;

    @Version
    private Long version;

    @Transient
    private Space space;

    @Builder
    public Channel(Long id, Long spaceId, String channelName, String channelType, Space space, Integer maxCapacity, Integer currentMemberCount) {
        this.id = id;
        this.spaceId = spaceId;
        this.channelName = channelName;
        this.channelType = channelType;
        this.space = space;
        this.maxCapacity = maxCapacity;
        this.currentMemberCount = currentMemberCount != null ? currentMemberCount : 0;
    }

    public enum Type {
        T, V
    }

    public Mono<Channel> incrementMemberCount() {
        if (this.currentMemberCount < this.maxCapacity) {
            this.currentMemberCount++;
            return Mono.just(this);
        } else {
            return Mono.error(new CustomException(ChannelErrorCode.CHANNEL_FULL));
        }
    }

    public Mono<Channel> decrementMemberCount() {
        if (this.currentMemberCount > 0) {
            this.currentMemberCount--;
            return Mono.just(this);
        } else {
            return Mono.error(new CustomException(ChannelErrorCode.CHANNEL_EMPTY));
        }
    }
}
