package com.echo.echo.domain.notification;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.common.exception.codes.CommonErrorCode;
import com.echo.echo.domain.notification.dto.NotificationDto;
import com.echo.echo.domain.notification.entity.Notification;
import com.echo.echo.domain.notification.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Mono<Notification> createNotification(NotificationDto dto) {
        return notificationRepository.save(
                Notification.builder()
                        .userId(dto.getUserId())
                        .spaceId(dto.getSpaceId())
                        .channelId(dto.getChannelId())
                        .eventType(dto.getEventType())
                        .notificationType(dto.getNotificationType())
                        .data(dto.getData())
                        .build()
                );
    }

    public Flux<Notification> getNotifications(Long userId, Notification.EventType eventType) {
        return notificationRepository.findAllByUserIdAndEventType(userId, eventType.name());
    }

    public Mono<Void> deleteNotification(String id) {
        return notificationRepository.deleteById(id);
    }
}
