package com.echo.echo.domain.video;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VideoHandler implements WebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionId = session.getId();
        sessions.put(sessionId, session);

        // 클라이언트에게 세션 ID 전송
        WebSocketMessage sessionIdMessage = session.textMessage("{\"sessionId\": \"" + sessionId + "\"}");
        session.send(Mono.just(sessionIdMessage)).subscribe();

        return session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .flatMap(message -> {
                // 받은 메시지를 다른 세션들에게 전파
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
    }
}