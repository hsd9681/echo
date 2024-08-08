package com.echo.echo.common.redis;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.common.exception.codes.CommonErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public Mono<String> getValue(String key) {
        return redisTemplate.opsForValue().get(key).map(String::valueOf);
    }

    public Mono<Boolean> setValue(String key, Object value) {
        return redisTemplate.opsForValue().set(key, value);
    }

    public Mono<Boolean> setValue(String key, Object value, Duration duration) {
        return redisTemplate.opsForValue().set(key, value)
                .then(redisTemplate.expire(key, duration));
    }

    public <T> Mono<T> getCacheValueGeneric(String key, Class<T> clazz) {
        return redisTemplate.opsForValue().get(key)
                .switchIfEmpty(Mono.empty())
                .flatMap(value -> Mono.just(objectMapper.convertValue(value, clazz)));
    }

    public Mono<Void> deleteValue(String key) {
        return redisTemplate.delete(key)
                .doOnError(err -> Mono.error(new CustomException(CommonErrorCode.NOT_FOUND_DATA)))
                .then();
    }
}
