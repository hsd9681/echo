package com.echo.echo.domain.text.dto;

import com.echo.echo.domain.text.entity.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TextResponse {

    private String id;
    private Long channelId;
    private String contents;
    private String username;
    private LocalDateTime createdAt;

    public TextResponse(Text text) {
        this.id = text.getId();
        this.contents = text.getContents();
        this.channelId = text.getChannelId();
        this.username = text.getUsername();
        this.createdAt = text.getCreatedAt();
    }
}
