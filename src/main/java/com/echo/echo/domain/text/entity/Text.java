package com.echo.echo.domain.text.entity;

import com.echo.echo.common.TimeStamp;
import com.echo.echo.domain.channel.entity.Channel;
import com.echo.echo.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Text extends TimeStamp {
    @Id
    private Long id;
    @Column
    private String contents;
    private Long userId;
    private Long channelId;
    @Transient
    private User user;
    @Transient
    private Channel channel;

    @Builder
    public Text(String contents, User user, Channel channel) {
        this.contents = contents;
        this.userId = user.getId();
        this.channelId = channel.getId();
        this.user = user;
        this.channel = channel;
    }
}
