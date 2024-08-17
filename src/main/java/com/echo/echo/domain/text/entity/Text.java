package com.echo.echo.domain.text.entity;

import com.echo.echo.common.TimeStamp;
import com.echo.echo.domain.text.dto.TextRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Document(collection = "text")
public class Text extends TimeStamp {
    @Id
    private String id;
    private TextType type;
    private String contents;
    private String username;
    private Long channelId;
    private Long userId;
    private String dmId; // 추가된 필드

    public Text(String contents, String username, Long userId, Long channelId, TextType type) {
        this.contents = contents;
        this.channelId = channelId;
        this.username = username;
        this.userId = userId;
        this.type = type;
    }

    public Text(TextRequest request, String username, Long userId, String dmId, TextType type ) {
        this.contents = request.getContents();
        this.dmId = dmId;
        this.username = username;
        this.type = type;
        this.userId = userId;
    }

    public void updateContents(TextRequest request) {
        this.contents = request.getContents();
    }

    public enum TextType {
        TEXT,
        FILE,
    }

}
