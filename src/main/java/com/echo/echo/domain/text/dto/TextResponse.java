package com.echo.echo.domain.text.dto;

import com.echo.echo.domain.text.entity.Text;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TextResponse {

    private String id;
    private Long channelId;
    private String contents;
    private String username;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    public TextResponse(Text text) {
        this.id = text.getId();
        this.contents = text.getContents();
        this.channelId = text.getChannelId();
        this.username = text.getUsername();
        this.createdAt = text.getCreatedAt();
    }

    @JsonCreator
    public TextResponse(@JsonProperty("id") String id,
                        @JsonProperty("channelId") Long channelId,
                        @JsonProperty("contents") String contents,
                        @JsonProperty("username") String username,
                        @JsonProperty("createdAt") LocalDateTime createdAt) {
        this.id = id;
        this.channelId = channelId;
        this.contents = contents;
        this.username = username;
        this.createdAt = createdAt;
    }
}
