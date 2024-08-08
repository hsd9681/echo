package com.echo.echo.common.redis;

import com.echo.echo.domain.text.controller.TextWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisListener {

    private final ReactiveRedisMessageListenerContainer listenerContainer;
    private final TextWebSocketHandler textWebSocketHandler;
    private final Sinks.Many<ReactiveSubscription.Message<String, String>> sink = Sinks.many().multicast().onBackpressureBuffer();
    private final ConcurrentHashMap<String, Disposable> topicSubscription = new ConcurrentHashMap<>();

    public Mono<Void> addTopic(String topicName) {
        ChannelTopic topic = new ChannelTopic(topicName);
        Disposable subscription = listenerContainer.receive(topic)
                .doOnNext(sink::tryEmitNext)
                .subscribe();
        topicSubscription.putIfAbsent(topicName, subscription);
        return Mono.empty();
    }

    public Mono<Void> removeTopic(String topicName) {
        Disposable subscription = topicSubscription.remove(topicName);
        if (subscription != null) {
            return Mono.fromRunnable(subscription::dispose)
                            .doOnTerminate(() -> log.info("{} topic 구독 해지", topicName)).then();
        } else {
            return Mono.empty();
        }
    }

    public Flux<ReactiveSubscription.Message<String, String>> getMessageFlux() {
        return sink.asFlux();
    }

    // Redis에서 Listen되고 있는 토픽이 추가될 때 case 추가하여 메시징 처리 로직으로 연결
    public Mono<Void> handleMessage(String topic, String body) {
        switch (topic) {
            case RedisConst.TEXT_CHANNEL_PREFIX:
                return textWebSocketHandler.sendText(body).then();
            default:
                return Mono.empty();
        }
    }
}
