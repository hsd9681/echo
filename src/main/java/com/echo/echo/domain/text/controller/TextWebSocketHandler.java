package com.echo.echo.domain.text.controller;

import com.echo.echo.domain.text.TextService;
import com.echo.echo.domain.text.dto.TextRequest;
import com.echo.echo.security.jwt.JwtProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "textHandler")
@Component
@RequiredArgsConstructor
public class TextWebSocketHandler implements WebSocketHandler {

    private final JwtProvider jwtProvider;
    private final ObjectMapper mapper;
    private final TextService textService;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Map<String, String> uriQuery = getParamFromSession(session);
        Long channelId = Long.valueOf(uriQuery.get("channel"));
        String token = uriQuery.get("token");

        String username = jwtProvider.getNickName(token);
        Long userId = jwtProvider.getUserId(token);

        Sinks.Many<String> sink = textService.getSink(channelId);
        Map<String, WebSocketSession> sessions = textService.getSessions(channelId);
        boolean isNewSession = sessions.putIfAbsent(session.getId(), session) == null;

        Flux<WebSocketMessage> outputMsg = sink.asFlux()
                .map(session::textMessage);

        if (isNewSession) {
            String joinMessage = String.format("%s님이 입장했습니다.", username);
            sink.tryEmitNext(joinMessage);
            outputMsg = Flux.concat(Flux.just(joinMessage).map(session::textMessage),
                    sink.asFlux().map(session::textMessage));
        }

        Mono<Void> output = session.send(outputMsg)
                .doOnError(e -> {
                    sessions.remove(session.getId());
                    session.close();
                });

        outputMsg.subscribe();

        Flux<String> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(payload -> {
                    try {
                        TextRequest request = mapper.readValue(payload, TextRequest.class);
                        return textService.sendText(request, username, userId, channelId)
                                .flatMap(response -> {
                                    try {
                                        String jsonResponse = mapper.writeValueAsString(response);
                                        sink.tryEmitNext(jsonResponse);
                                        return Mono.empty();
                                    } catch (JsonProcessingException ex) {
                                        return Mono.just("메세지 처리 중 오류 발생");
                                    }
                                });
                    } catch (Exception e) {
                        return Mono.just("메세지 처리 중 오류 발생");
                    }
                })
                .doOnError(e -> {
                    sessions.remove(session.getId());
                    session.close();
                })
                .doFinally(signal -> {
                    sessions.remove(session.getId());
                    sink.tryEmitNext(String.format("%s님이 퇴장했습니다.", username));
                    session.close();
                });

        return Mono.when(input, output);
    }

    private Map<String, String> getParamFromSession(WebSocketSession session) {
        String query = session.getHandshakeInfo().getUri().getQuery();
        Map<String, String> queryParam = new HashMap<>();

        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = keyValue[0];
            String value = keyValue.length > 1 ? keyValue[1] : "";
            queryParam.put(key, value);
        }

        return queryParam;
    }
}