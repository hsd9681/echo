package com.echo.echo.common.redis;

import com.echo.echo.domain.notification.SseProcessor;
import com.echo.echo.domain.text.controller.TextWebSocketHandler;
import com.echo.echo.domain.thread.service.ThreadWebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisListener {

    private final TextWebSocketHandler textWebSocketHandler;
    private final SseProcessor sseProcessor;
    private final ThreadWebSocketService threadWebsocketService;

    // Redis에서 Listen되고 있는 토픽이 추가될 때 case 추가하여 메시징 처리 로직으로 연결
    public Mono<Void> handleMessage(RedisConst topic, String body) {
        switch (topic) {
            case TEXT:
                return textWebSocketHandler.sendText(body).then();
            case TYPING:
                return textWebSocketHandler.sendTyping(body).then();
            case SSE:
                return sseProcessor.redisListen(body).then();
            case THREAD:
                return threadWebsocketService.emitMessage(body);
            default:
                return Mono.empty();
        }
    }
}
