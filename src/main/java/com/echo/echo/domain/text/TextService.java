package com.echo.echo.domain.text;

import com.echo.echo.domain.text.dto.TextRequest;
import com.echo.echo.domain.text.dto.TextResponse;
import com.echo.echo.domain.text.entity.Text;
import com.echo.echo.domain.text.repository.TextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TextService {

    private final TextRepository repository;
    private final Map<Long, Sinks.Many<String>> channelSinks = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, WebSocketSession>> channelSessions = new ConcurrentHashMap<>();

    public Sinks.Many<String> getSink(Long channelId) {
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

    public Mono<TextResponse> sendText(TextRequest request, String username, Long userId, Long channelId) {
        Text text = new Text(request, username, userId, channelId);
        return repository.save(text)
                .map(TextResponse::new);
    }
}
