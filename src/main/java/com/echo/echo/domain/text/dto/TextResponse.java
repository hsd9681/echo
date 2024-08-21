package com.echo.echo.domain.text.dto;

import com.echo.echo.domain.text.entity.Text;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextResponse {

    private String id;
    private Long channelId;
    private Text.TextType textType;
    private String dmId;
    private String contents;
    private Long userId;
    private String username;
    private TextResponse.HandleType handleType;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime modifiedAt;

    public TextResponse(Text text) {
        this.id = text.getId();
        this.textType = text.getType();
        this.contents = text.getContents();
        this.channelId = text.getChannelId();
        this.dmId = text.getDmId();
        this.userId = text.getUserId();
        this.username = text.getUsername();
        this.createdAt = text.getCreatedAt();
        this.modifiedAt = text.getModifiedAt();
    }

    @JsonCreator
    public TextResponse(@JsonProperty("id") String id,
                        @JsonProperty("channelId") Long channelId,
                        @JsonProperty("textType") Text.TextType textType,
                        @JsonProperty("dmId") String dmId,
                        @JsonProperty("contents") String contents,
                        @JsonProperty("username") String username,
                        @JsonProperty("handleType") TextResponse.HandleType handleType,
                        @JsonProperty("createdAt") LocalDateTime createdAt,
                        @JsonProperty("modifiedAt") LocalDateTime modifiedAt) {
        this.id = id;
        this.channelId = channelId;
        this.textType = textType;
        this.dmId = dmId;
        this.contents = contents;
        this.username = username;
        this.handleType = handleType;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public enum HandleType {
        CREATED, UPDATED, DELETED
    }

}
