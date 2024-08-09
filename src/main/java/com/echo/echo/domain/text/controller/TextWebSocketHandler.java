package com.echo.echo.domain.text.controller;

import com.echo.echo.common.redis.RedisConst;
import com.echo.echo.common.redis.RedisPublisher;
import com.echo.echo.common.util.ObjectStringConverter;
import com.echo.echo.domain.text.TextService;
import com.echo.echo.domain.text.dto.TextRequest;
import com.echo.echo.domain.text.dto.TextResponse;
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
        Long channelId = Long.valueOf(uriQuery.get("channel"));
        String token = uriQuery.get("token");

        String username = jwtProvider.getNickName(token);
        Long userId = jwtProvider.getUserId(token);

        Sinks.Many<TextResponse> textResponseSink = textService.getSink(channelId);
        Flux<TextResponse> textResponseFlux = textResponseSink.asFlux();

        Flux<WebSocketMessage> sendMessagesFlux = textResponseFlux
                .flatMap(objectStringConverter::objectToString)
                .map(session::textMessage)
                .doOnError(throwable -> log.error("웹소켓 메시지 변환 간 오류 발생", throwable));

        Mono<Void> output = session.send(sendMessagesFlux);

        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(payload -> {
                    Mono<TextRequest> request = objectStringConverter.stringToObject(payload, TextRequest.class);
                    return textService.sendText(request, username, userId, channelId)
                            .flatMap(response -> {
                                ChannelTopic topic = new ChannelTopic(RedisConst.TEXT.name());
                                return redisPublisher.publish(topic, response);
                            });
                })
                .doOnSubscribe(subscription -> {
                    textService.loadTextByChannelId(channelId)
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

    public Mono<Sinks.EmitResult> sendText(String body) {
        return Mono.fromSupplier(() -> objectStringConverter.stringToObject(body, TextResponse.class))
                .flatMap(response -> response.map(res ->
                                textService.getSink(res.getChannelId()).tryEmitNext(res)))
                .doOnSuccess(emitResult -> {
                    if (emitResult.isFailure()) {
                        log.error("Redis Sub 메시지 전송 실패: {}", body);
                    }
                });
    }
}
