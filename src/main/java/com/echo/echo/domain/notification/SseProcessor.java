package com.echo.echo.domain.notification;

import com.echo.echo.common.util.ObjectStringConverter;
import com.echo.echo.domain.notification.dto.NotificationResponseDto;
import com.echo.echo.domain.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j(topic = "SseProcessor")
@RequiredArgsConstructor
@Component
public class SseProcessor {
    private final ObjectStringConverter objectStringConverter;
    private final Map<Long, Sinks.Many<ServerSentEvent<NotificationResponseDto>>> sinks = new ConcurrentHashMap<>();
    private final static String PING_TYPE = "ping";
    private final static int PING_SEC = 40;

    /**
     * redis에서 메시지가 발생하면 실행
     * @param body 발행된 메시지
     */
    public Mono<Void> redisListen(String body) {
        return objectStringConverter.stringToObject(body, NotificationResponseDto.class)
                .flatMap(responseDto -> {
                    if (sinks.containsKey(responseDto.getUserId())) {
                        log.info("sendId: {}", responseDto.getUserId());
                        messageSend(responseDto);
                    }
                    return Mono.empty();
                });
    }

    /**
     * 메시지 발행
     * @param dto 메시지 정보
     */
    public void messageSend(NotificationResponseDto dto) {
        sinks.get(dto.getUserId()).tryEmitNext(ServerSentEvent.<NotificationResponseDto>builder()
                .data(dto)
                .build());
    }

    /**
     * 연결이 끊기지 않도록 주기적으로 ping 메시지 전송
     */
    private Flux<ServerSentEvent<NotificationResponseDto>> sendPing() {
        return Flux.interval(Duration.ofSeconds(PING_SEC))
                .map(unused -> ServerSentEvent.<NotificationResponseDto>builder()
                        .data(new NotificationResponseDto(PING_TYPE))
                        .build());
    }

    /**
     * 입장 시 실행
     * 메시지 발행이 일어나면 유저에게 전달한다.
     * @param id 입장한 유저의 아이디 (고유값)
     */
    protected Flux<ServerSentEvent<NotificationResponseDto>> connect(Long id) {
        log.info("Connecting to {}", id);

        if (sinks.containsKey(id)) {
            return sinks.get(id).asFlux();
        }

        sinks.put(id, Sinks.many().multicast().onBackpressureBuffer());
        return Flux.merge(sinks.get(id).asFlux(),
                sendPing())
                .doOnCancel(() -> {
                    log.info("Notification service canceled" + id);
                    sinks.remove(id);
                })
                .doFinally(data -> log.info("[{}] 연결이 해지되었습니다.", id));
    }
}
