package com.echo.echo.domain.text;

import com.echo.echo.domain.text.dto.TextRequest;
import com.echo.echo.domain.text.dto.TextResponse;
import com.echo.echo.domain.text.entity.Text;
import com.echo.echo.domain.text.repository.TextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextService {

    private final TextRepository repository;
    private final Map<Long, Sinks.Many<TextResponse>> channelSinks = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, WebSocketSession>> channelSessions = new ConcurrentHashMap<>();

    public Sinks.Many<TextResponse> getSink(Long channelId) {
        return channelSinks.compute(channelId, (id, existingSink) -> {
            if (existingSink == null || existingSink.currentSubscriberCount() == 0) {
                return Sinks.many().multicast().onBackpressureBuffer();
            }
            return existingSink;
        });
    }

    public Map<String, WebSocketSession> getSessions(Long channelId) {
        return channelSessions.computeIfAbsent(channelId, id -> new ConcurrentHashMap<>());
    }

    public Mono<TextResponse> sendText(Mono<TextRequest> request, String username, Long userId, Long channelId) {
        return request
                .map(textRequest -> new Text(textRequest, username, userId, channelId))
                .flatMap(repository::save)
                .map(TextResponse::new);

    }

    public Flux<TextResponse> loadTextByChannelId(Long channelId) {
        return repository.findAllByChannelId(channelId)
                        .map(TextResponse::new);
    }
}
