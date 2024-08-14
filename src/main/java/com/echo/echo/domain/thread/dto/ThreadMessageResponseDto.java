package com.echo.echo.domain.thread.dto;

import com.echo.echo.domain.thread.entity.ThreadMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ThreadMessageResponseDto {
    @JsonProperty
    private Long id;
    @JsonProperty
    private Long threadId;
    @JsonProperty
    private Long userId;
    @JsonProperty
    private String nickname;
    @JsonProperty
    private String content;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;

    public ThreadMessageResponseDto(ThreadMessage threadMessage) {
        this.id = threadMessage.getId();
        this.threadId = threadMessage.getThreadId();
        this.userId = threadMessage.getAuthorId();
        this.nickname = threadMessage.getUser().getNickname();
        this.content = threadMessage.getContent();
        this.createdAt = threadMessage.getCreatedAt();
        this.modifiedAt = threadMessage.getModifiedAt();
    }

    public ThreadMessageResponseDto(ThreadMessage threadMessage, String nickname) {
        this.id = threadMessage.getId();
        this.threadId = threadMessage.getThreadId();
        this.userId = threadMessage.getAuthorId();
        this.nickname = nickname;
        this.content = threadMessage.getContent();
        this.createdAt = threadMessage.getCreatedAt();
        this.modifiedAt = threadMessage.getModifiedAt();
    }
}
