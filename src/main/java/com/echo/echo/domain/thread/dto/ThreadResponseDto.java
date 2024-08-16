package com.echo.echo.domain.thread.dto;

import com.echo.echo.domain.thread.entity.Thread;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ThreadResponseDto {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String textId;
    @JsonProperty
    private String nickname;
    @JsonProperty
    private String status;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;

    public ThreadResponseDto(Thread thread) {
        this.id = thread.getId();
        this.textId = thread.getTextId();
        this.nickname = thread.getUser().getNickname();
        this.status = Thread.Status.values()[thread.getStatus()].name();
        this.createdAt = thread.getCreatedAt();
        this.modifiedAt = thread.getModifiedAt();
    }

    public ThreadResponseDto(Thread thread, String nickname) {
        this.id = thread.getId();
        this.textId = thread.getTextId();
        this.nickname = nickname;
        this.status = Thread.Status.values()[thread.getStatus()].name();
        this.createdAt = thread.getCreatedAt();
        this.modifiedAt = thread.getModifiedAt();
    }
}
