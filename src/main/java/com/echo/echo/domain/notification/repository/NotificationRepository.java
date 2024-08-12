package com.echo.echo.domain.notification.repository;

import com.echo.echo.domain.notification.entity.Notification;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {
    Flux<Notification> findAllByUserIdAndEventType(Long UserId, String eventType);
}