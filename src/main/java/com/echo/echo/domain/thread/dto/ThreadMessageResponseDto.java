package com.echo.echo.domain.thread.dto;

import com.echo.echo.domain.thread.entity.Thread;
import com.echo.echo.domain.thread.entity.ThreadMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ThreadMessageResponseDto {
    @JsonProperty
    private Long id;
    @JsonProperty
    private Long userId;
    @JsonProperty
    private String nickname;
    @JsonProperty
    private String content;

    public ThreadMessageResponseDto(ThreadMessage threadMessage) {
        this.id = threadMessage.getId();
        this.userId = threadMessage.getAuthorId();
        this.nickname = threadMessage.getUser().getNickname();
        this.content = threadMessage.getContent();
    }

    public ThreadMessageResponseDto(ThreadMessage threadMessage, String nickname) {
        this.id = threadMessage.getId();
        this.userId = threadMessage.getAuthorId();
        this.nickname = nickname;
        this.content = threadMessage.getContent();
    }
}
