package com.echo.echo.domain.notification.dto;

import com.echo.echo.domain.notification.entity.Notification;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NotificationResponseDto {

    private Long userId;
    @JsonProperty
    private String eventType;
    @JsonProperty
    private String notificationType;
    @JsonProperty
    private Object data;

    @Builder
    public NotificationResponseDto(Long userId, Notification.EventType eventType, Notification.NotificationType notificationType, Object data) {
        this.userId = userId;
        this.eventType = eventType.name();
        this.notificationType = notificationType.name();
        this.data = data;
    }

    public NotificationResponseDto(String eventType) {
        this.eventType = eventType;
    }

}
