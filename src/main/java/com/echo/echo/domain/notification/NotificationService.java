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

    // crud부터 만들자 ,  업데이트는 만들 이유가 없다...?

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

    public Flux<Notification> getNotifications(Long id) {
        return Flux.empty();
    }

    public Mono<Notification> deleteNotification(Long id) {
        return Mono.empty();
    }
}
