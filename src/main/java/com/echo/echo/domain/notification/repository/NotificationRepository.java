package com.echo.echo.domain.notification.repository;

import com.echo.echo.domain.notification.entity.Notification;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {
    // 각자 space의 채널인 text count를 가져와야한다.
//    Mono<Notification> countAllBy
}