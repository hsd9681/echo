package com.echo.echo.domain.text.entity;

import com.echo.echo.domain.text.dto.TextRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Document(collection = "text")
public class Text {
    @Id
    private String id;
    private String contents;
    private String username;
    private Long channelId;
    private Long userId;
    private LocalDateTime createdAt;

    public Text(TextRequest request, String username, Long userId, Long channelId) {
        this.contents = request.getContents();
        this.channelId = channelId;
        this.username = username;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
}
