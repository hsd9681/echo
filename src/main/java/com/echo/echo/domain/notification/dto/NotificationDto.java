package com.echo.echo.domain.notification.dto;

import com.echo.echo.domain.notification.entity.Notification;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NotificationDto<T> {
    @JsonProperty
    private String eventType;
    @JsonProperty
    private String notificationType;
    @JsonProperty
    private T data;

    @Builder
    public NotificationDto(Notification.EventType eventType, Notification.NotificationType notificationType, T data) {
        this.eventType = eventType.name();
        this.notificationType = notificationType.name();
        this.data = data;
    }
}
