package com.echo.echo.domain.notification.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private Object data;

    public enum EventType {
        CREATED, UPDATED, DELETED
    }

    public enum NotificationType {
        SPACE, CHANNEL, TEXT
    }

    @Builder
    public Notification(String id, Long userId, Long spaceId, Long channelId, EventType eventType, NotificationType notificationType, Object data) {
        this.id = id;
        this.userId = userId;
        this.spaceId = spaceId;
        this.channelId = channelId;
        this.eventType = eventType.name();
        this.notificationType = notificationType.name();
        this.data = data;
    }
}
