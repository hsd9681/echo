package com.echo.echo.domain.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketMessage;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.echo.echo.domain.channel.ChannelService;

@Component
@RequiredArgsConstructor
public class VideoHandler implements WebSocketHandler {

    private final ChannelService channelService;
    private static final Logger log = LoggerFactory.getLogger(VideoHandler.class);
    private final Map<String, Map<String, WebSocketSession>> channels = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionId = session.getId();
        String path = session.getHandshakeInfo().getUri().getPath();
        String channelId = extractChannelId(path);
		log.debug("channelID: {}, sessionID: {}, message: {}", channelId, sessionId, session.receive().toString());

        return channelService.checkAndIncrementMemberCount(Long.valueOf(channelId))
            .then(Mono.defer(() -> {
                channels.putIfAbsent(channelId, new ConcurrentHashMap<>());
                channels.get(channelId).put(sessionId, session);

                // 클라이언트에게 세션 ID 전송
                WebSocketMessage sessionIdMessage = session.textMessage("{\"sessionId\": \"" + sessionId + "\"}");
                return session.send(Mono.just(sessionIdMessage))
                    .then(session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .flatMap(message -> {
                            if (message.contains("$p&ing")) {
                                return Mono.empty();
                            }
                            // 받은 메시지를 다른 세션들에게 전파
                            return Flux.fromIterable(channels.get(channelId).values())
                                .filter(WebSocketSession::isOpen)
                                .filter(s -> !s.getId().equals(sessionId))
                                .flatMap(s -> {
                                    WebSocketMessage outboundMessage = s.textMessage(message);
                                    return s.send(Mono.just(outboundMessage));
                                })
                                .then();
                        })
                        .doFinally(signalType -> {
                            channels.get(channelId).remove(sessionId);
                            if (channels.get(channelId).isEmpty()) {
                                channels.remove(channelId);
                            }
                            channelService.decrementMemberCount(Long.valueOf(channelId)).subscribe();
                        })
                        .then());
            }))
            .onErrorResume(e -> {
                log.error(e.getMessage());
                WebSocketMessage errorMessage = session.textMessage("{\"msg\": \"" + e.getMessage() + "\"}");
                return session.send(Mono.just(errorMessage))
                    .then(session.close());
            });
    }

    private String extractChannelId(String path) {
        String[] segments = path.split("/");
        return segments.length > 3 ? segments[3] : "1";
    }

}