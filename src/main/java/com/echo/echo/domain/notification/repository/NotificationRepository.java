package com.echo.echo.domain.notification.repository;

import com.echo.echo.domain.notification.entity.Notification;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {

    Flux<Notification> findAllByUserIdAndEventType(Long userId, String eventType);

    Mono<Notification> findByUserIdAndChannelIdAndNotificationType(Long userId, Long channelId, String notificationType);

    Flux<Notification> findAllByUserIdAndNotificationType(Long userId, String notificationType);

    Mono<Boolean> existsByUserIdAndChannelIdAndEventTypeAndNotificationType(Long userId, Long channelId, String eventType, String notificationType);

    Mono<Void> deleteByUserIdAndChannelId(Long userId, Long channelId);

}