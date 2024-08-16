package com.echo.echo.domain.notification.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Document(collection = "notification")
public class Notification {
    @Id
    private String id;
    private Long userId;
    private Long spaceId;
    private Long channelId;
    private String eventType;
    private String notificationType;
    private String message;

    public enum EventType {
        CREATED, UPDATED, DELETED
    }

    public enum NotificationType {
        CHANNEL, TEXT
    }

    @Builder
    public Notification(String id, Long userId, Long spaceId, Long channelId, EventType eventType, NotificationType notificationType, String message) {
        this.id = id;
        this.userId = userId;
        this.spaceId = spaceId;
        this.channelId = channelId;
        this.eventType = eventType.name();
        this.notificationType = notificationType.name();
        this.message = message;
    }

//    public Notification(String id, Long userId, Long spaceId, Long channelId, String eventType, String notificationType, String messageId) {
//        this.id = id;
//        this.userId = userId;
//        this.spaceId = spaceId;
//        this.channelId = channelId;
//        this.eventType = eventType;
//        this.notificationType = notificationType;
//        this.messageId = messageId;
//    }
}
