package com.echo.echo.domain.text.dto;

import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TextResponse {

    private Long id;
    private Long channelId;
    private String contents;
    private Long userId;
    private Instant createdAt;

    public TextResponse(Row row) {

        this.setId(row.get("text_id", Long.class));
        this.setChannelId(row.get("channel_id", Long.class));
        this.setContents(row.get("contents", String.class));
        this.setUserId(row.get("user_id", Long.class));
        this.setCreatedAt(row.get("created_at", Instant.class));
    }

    public boolean isNotFromUser(Long userId) {
        return !Objects.equals(this.userId, userId);
    }
}
