package com.echo.echo.domain.text.controller;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.common.exception.codes.CommonErrorCode;
import com.echo.echo.domain.text.TextService;
import com.echo.echo.domain.text.dto.TextRequest;
import com.echo.echo.domain.text.dto.TextResponse;
import com.echo.echo.security.jwt.JwtProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        Flux<String> input = session.receive()
                .doOnSubscribe(subscription -> {
                    textService.loadTextByChannelId(channelId)
                            .map(this::objectToString)
                            .map(session::textMessage)
                            .subscribe(messages -> session.send(Mono.just(messages)).subscribe());
                })
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(payload -> {
                    TextRequest request = this.payloadToObject(payload, TextRequest.class);
                    return textService.sendText(request, username, userId, channelId)
                            .flatMap(response -> {
                                String responseString = this.objectToString(response);
                                sink.tryEmitNext(responseString);
                                return Mono.just(responseString);
                            });
                })
                .doOnError(e -> {
                    sessions.remove(session.getId());
                    session.close();
                })
                .doFinally(signal -> {
                    sessions.remove(session.getId());
                    session.close();
                });

        Mono<Void> output = session.send(sink.asFlux().map(session::textMessage));

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

    private String objectToString(TextResponse response) {
        try {
            System.out.println(mapper.writeValueAsString(response));
            return mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new CustomException(CommonErrorCode.FAIL);
        }
    }

    private <T> T payloadToObject(String payload, Class<T> readClass) {
        try {
            return mapper.readValue(payload, readClass);
        } catch (JsonProcessingException e) {
            throw new CustomException(CommonErrorCode.FAIL);
        }
    }
}
