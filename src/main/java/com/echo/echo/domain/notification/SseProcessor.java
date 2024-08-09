package com.echo.echo.domain.notification;

import com.echo.echo.domain.notification.dto.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j(topic = "SseProcessor")
@Component
public class SseProcessor {
    private final Map<Long, Sinks.Many<ServerSentEvent<NotificationDto>>> sinks = new ConcurrentHashMap<>();

    /**
     * 메시지 발행
     * @param id 발행할 유저 아이디
     * @param dto 메시지 정보
     */
    public Mono<Void> messageSend(Long id, NotificationDto dto) {
        if(sinks.containsKey(id)) {
            log.info("sendId: {}", id);
            sinks.get(id).tryEmitNext(ServerSentEvent.<NotificationDto>builder()
                    .data(dto)
                    .build());
        }
        return Mono.empty();
    }

    /**
     * 입장 시 실행
     * 메시지 발행이 일어나면 유저에게 전달한다.
     * @param id 입장한 유저의 아이디 (고유값)
     */
    protected Flux<ServerSentEvent<NotificationDto>> connect(Long id) {
        log.info("Connecting to {}", id);

        if (sinks.containsKey(id)) {
            return sinks.get(id).asFlux();
        }

        sinks.put(id, Sinks.many().multicast().onBackpressureBuffer());
        return sinks.get(id).asFlux()
                .doOnCancel(() -> {
                    log.info("Notification service canceled" + id);
                    sinks.remove(id);
                });
    }
}
