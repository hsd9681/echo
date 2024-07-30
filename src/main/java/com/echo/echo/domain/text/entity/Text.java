package com.echo.echo.domain.text.entity;

import com.echo.echo.common.TimeStamp;
import com.echo.echo.domain.channel.entity.Channel;
import com.echo.echo.domain.text.dto.TextRequest;
import com.echo.echo.domain.user.entity.User;
import io.r2dbc.spi.Row;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import reactor.core.publisher.Mono;

import javax.swing.plaf.PanelUI;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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

    public Text(TextRequest request) {
        this.contents = request.getContents();
        this.userId = 1L;
        this.channelId = 1L;
        this.user = null;
        this.channel = null;
    }

    public Text(Row row) {
        this.id = row.get("id", Long.class);
        this.contents = row.get("contents", String.class);
        this.userId = row.get("userId", Long.class);
        this.channelId = row.get("channelId", Long.class);
    }
}
