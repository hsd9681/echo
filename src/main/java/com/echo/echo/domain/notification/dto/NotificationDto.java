package com.echo.echo.domain.notification.dto;

import com.echo.echo.domain.notification.entity.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NotificationDto {
    private String id;
    private Long userId;
    private Long spaceId;
    private Long channelId;
    private Notification.EventType eventType;
    private Notification.NotificationType notificationType;
    private String message;

    @Builder
    public NotificationDto(String id, Long userId, Long spaceId, Long channelId, Notification.EventType eventType, Notification.NotificationType notificationType, String message) {
        this.id = id;
        this.userId = userId;
        this.spaceId = spaceId;
        this.channelId = channelId;
        this.eventType = eventType;
        this.notificationType = notificationType;
        this.message = message;
    }

    public NotificationDto(Notification notification) {
        this.id = notification.getId();
        this.userId = notification.getUserId();
        this.spaceId = notification.getSpaceId();
        this.channelId = notification.getChannelId();
        this.eventType = Notification.EventType.valueOf(notification.getEventType());
        this.notificationType = Notification.NotificationType.valueOf(notification.getNotificationType());
        this.message = notification.getMessage();
    }
}
