package com.echo.echo.domain.notification;

import com.echo.echo.domain.notification.dto.NotificationDto;
import com.echo.echo.domain.notification.entity.Notification;
import com.echo.echo.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Mono<Void> createNotification(NotificationDto dto) {
        return userAndChannelDataExists(dto.getUserId(), dto.getChannelId())
                .filter(isExists -> !isExists)
                .flatMap(isExists  -> notificationRepository.save(
                        Notification.builder()
                                .userId(dto.getUserId())
                                .spaceId(dto.getSpaceId())
                                .channelId(dto.getChannelId())
                                .eventType(dto.getEventType())
                                .notificationType(dto.getNotificationType())
                                .message(dto.getMessage())
                                .build()
                ))
                .then();
    }

    /**
     * 채팅 메시지에 해당하는 알림 메시지 정보를 가져온다.
     * @param userId 해당하는 유저
     */
    public Flux<Notification> getNotificationsTextByUserId(Long userId) {
        return notificationRepository.findAllByUserIdAndNotificationType(userId, Notification.NotificationType.TEXT.name());
    }

    public Flux<Notification> getNotifications(Long userId, Notification.EventType eventType) {
        return notificationRepository.findAllByUserIdAndEventType(userId, eventType.name());
    }

    public Mono<Void> deleteNotification(String id) {
        return notificationRepository.deleteById(id);
    }

    private Mono<Boolean> userAndChannelDataExists(Long userId, Long channelId) {
        return notificationRepository.existsByUserIdAndChannelIdAndEventTypeAndNotificationType(userId, channelId, Notification.EventType.CREATED.name(), Notification.NotificationType.TEXT.name());
    }
}
