package com.echo.echo.domain.text.entity;

import com.echo.echo.common.TimeStamp;
import com.echo.echo.domain.channel.entity.Channel;
import com.echo.echo.domain.text.dto.TextRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(value = "text")
@Getter
public class Text extends TimeStamp {
    @Id
    private Long id;
    @Column
    private String contents;
    private String username;
    private Long channelId;
    private Long userId;

    @Builder
    public Text(String contents, String username, Channel channel) {
        this.contents = contents;
        this.username = username;
        this.channelId = channel.getId();
    }

    public Text(TextRequest request, String username, Long userId, Long channelId) {
        this.contents = request.getContents();
        this.channelId = channelId;
        this.username = username;
        this.userId = userId;
    }
}
