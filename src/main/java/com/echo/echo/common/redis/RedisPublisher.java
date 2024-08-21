package com.echo.echo.common.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    public <T> Mono<Void> publish(ChannelTopic topic, T t) {
        return Mono.defer(() -> reactiveRedisTemplate.convertAndSend(topic.getTopic(), t)
                .doOnSuccess(success -> {
                    if (success > 0)
                        log.debug("메시지 퍼블리싱 성공");
                    else
                        log.warn("퍼블리싱 메시지를 받을 구독자가 없음");
                })
                .doOnError(throwable -> log.error("메시지 퍼블리싱 오류", throwable))
                .then());
    }

}
