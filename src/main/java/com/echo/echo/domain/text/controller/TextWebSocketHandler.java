package com.echo.echo.domain.text.controller;

import java.util.*;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.echo.echo.common.redis.RedisConst;
import com.echo.echo.common.redis.RedisPublisher;
import com.echo.echo.common.util.ObjectStringConverter;
import com.echo.echo.domain.text.TextService;
import com.echo.echo.domain.text.dto.TextRequest;
import com.echo.echo.domain.text.dto.TextResponse;
import com.echo.echo.domain.text.dto.TypingRequest;
import com.echo.echo.domain.text.dto.TypingResponse;
import com.echo.echo.domain.text.entity.Text;
import com.echo.echo.security.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "textHandler")
@RequiredArgsConstructor
public class TextWebSocketHandler implements WebSocketHandler {

    private final JwtProvider jwtProvider;
    private final TextService textService;
    private final ObjectStringConverter objectStringConverter;
    private final RedisPublisher redisPublisher;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Map<String, String> uriQuery = getParamFromSession(session);
        String channelId = uriQuery.get("channel");
        String dmId = uriQuery.get("dmId"); // DM 관련 파라미터
        String token = uriQuery.get("token");

        String username = jwtProvider.getNickName(token);
        Long userId = jwtProvider.getUserId(token);

        // Determine which Sinks to use based on presence of dmId
        Sinks.Many<TextResponse> textResponseSink = dmId != null ?
                textService.getDmSink(dmId) : textService.getSink(Long.valueOf(channelId));

        Flux<TextResponse> textResponseFlux = textResponseSink.asFlux();

        Flux<WebSocketMessage> sendMessagesFlux = textResponseFlux
                .flatMap(objectStringConverter::objectToString)
                .map(session::textMessage)
                .doOnError(throwable -> log.error("웹소켓 메시지 변환 간 오류 발생", throwable));

        Mono<Void> output = session.send(sendMessagesFlux);

        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(payload -> {
                    if (payload.contains("$p&ing")) {
                        return Mono.empty();
                    }
                    if (payload.contains("typing") && channelId != null) {
                        Mono<TypingRequest> request = objectStringConverter.stringToObject(payload, TypingRequest.class);
                        return textService.sendTyping(request, username, Long.valueOf(channelId))
                            .flatMap(response -> {
                                ChannelTopic topic = new ChannelTopic(RedisConst.TYPING.name());
                                return redisPublisher.publish(topic, response);
                            });
                    } else {
                        Mono<TextRequest> request = objectStringConverter.stringToObject(payload, TextRequest.class);
                        return (dmId != null ?
                                textService.sendTextToDm(request, username, userId, dmId, Text.TextType.TEXT) :
                                textService.sendText(request, username, userId, Long.valueOf(channelId), Text.TextType.TEXT))
                            .flatMap(response -> {
                                ChannelTopic topic = new ChannelTopic(RedisConst.TEXT.name());
                                return redisPublisher.publish(topic, response);
                            });
                    }
                })
                .doOnSubscribe(subscription -> {
                    (dmId != null ?
                            textService.loadTextByDmId(dmId) :
                            textService.loadTextByChannelId(Long.valueOf(channelId)))
                            .flatMap(objectStringConverter::objectToString)
                            .map(session::textMessage)
                            .flatMap(messages -> session.send(Mono.just(messages)))
                            .then()
                            .subscribe();
                }).then()
                .doOnError(e -> {
                    session.close();
                })
                .doFinally(signal -> {
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

    public Mono<Sinks.EmitResult> sendTyping(String body) {
        return Mono.fromSupplier(() -> objectStringConverter.stringToObject(body, TypingResponse.class))
            .flatMap(response -> response.map(res ->
                    textService.getSink(res.getChannelId()).tryEmitNext(res)))
            .doOnSuccess(emitResult -> {
                if (emitResult.isFailure()) {
                    log.error("타이핑 상태 전송 실패: {}", body);
                }
            });
    }

    public Mono<Sinks.EmitResult> sendText(String body) {
        return Mono.fromSupplier(() -> objectStringConverter.stringToObject(body, TextResponse.class))
                .flatMap(response -> response.map(res ->
                        (res.getDmId() != null ?
                                textService.getDmSink(res.getDmId()) :
                                textService.getSink(res.getChannelId())).tryEmitNext(res))) // DM ID 또는 채널 ID 사용
                .doOnSuccess(emitResult -> {
                    if (emitResult.isFailure()) {
                        log.error("Redis Sub 메시지 전송 실패: {}", body);
                    }
                });
    }
}
