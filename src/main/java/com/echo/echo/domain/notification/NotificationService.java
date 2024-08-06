package com.echo.echo.domain.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class NotificationService {
    private final Map<Long, Sinks.Many<ServerSentEvent<String>>> sinks = new ConcurrentHashMap<>();

    public Mono<Void> personalSend(Long id, String message) {
        if(sinks.containsKey(id)) {
            sinks.get(id).tryEmitNext(ServerSentEvent.<String>builder()
                    .data(message)
                    .build());
        }
        return Mono.empty();
    }

    public Flux<ServerSentEvent<String>> connect(Long id) {
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
