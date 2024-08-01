package com.echo.echo.domain.voice;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class SignalHandler implements WebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionId = session.getId();
        sessions.put(sessionId, session);

        Mono<Void> input = session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .flatMap(message -> {
                // Broadcast received message to all other sessions
                return Flux.fromIterable(sessions.values())
                    .filter(WebSocketSession::isOpen)
                    .filter(s -> !s.getId().equals(sessionId))
                    .flatMap(s -> {
                        WebSocketMessage outboundMessage = s.textMessage(message);
                        return s.send(Mono.just(outboundMessage)); // 개별 메시지 전송을 체인으로 엮음
                    })
                    .then();
            })
            .doFinally(signalType -> sessions.remove(sessionId))
            .then();

        return input;
    }
}