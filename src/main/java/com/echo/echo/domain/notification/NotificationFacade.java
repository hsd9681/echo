package com.echo.echo.domain.notification;

import com.echo.echo.domain.notification.dto.NotificationDto;
import com.echo.echo.domain.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class NotificationFacade {
    private final NotificationService notificationService;

    public Mono<Void> createNotification(NotificationDto dto) {
        return notificationService.createNotification(dto);
    }

    public Flux<Notification> getNotificationsTextByUserId(Long userId) {
        return notificationService.getNotificationsTextByUserId(userId);
    }

    public Mono<Void> deleteNotification(Long userId, Long channelId) {
        return notificationService.deleteNotification(userId, channelId);
    }
}
