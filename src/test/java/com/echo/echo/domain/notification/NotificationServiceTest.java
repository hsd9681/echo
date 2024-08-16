package com.echo.echo.domain.notification;

import com.echo.echo.domain.notification.dto.NotificationDto;
import com.echo.echo.domain.notification.entity.Notification;
import com.echo.echo.domain.text.controller.TextWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NotificationServiceTest {

    @Autowired
    NotificationService notificationService;

    @Test
    void test1() throws InterruptedException {
        // 해당 유저, 채널, eventType, 알람 유형으로된 데이터가 있는지 확인 후 데이터가 존재하면 저장하지 않는지 확인한다.
        NotificationDto dto = NotificationDto.builder()
                .userId(1L)
                .spaceId(1L)
                .channelId(14L)
                .eventType(Notification.EventType.CREATED)
                .notificationType(Notification.NotificationType.TEXT)
                .message("message2222")
                .build();

        notificationService.createNotification(dto)
                .subscribe();

        Thread.sleep(1000);
    }

    @Test
    void test2() throws InterruptedException{
        notificationService.getNotificationsTextByUserId(2L)
                .doOnNext(data -> System.out.println("data: " + data.getMessage()))
                .subscribe();

        Thread.sleep(1000);
    }
}