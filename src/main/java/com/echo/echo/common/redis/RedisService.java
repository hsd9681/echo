package com.echo.echo.common.redis;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.common.exception.codes.CommonErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisService {

    private final ReactiveRedisOperations<String, Object> redisOps;
    private final ObjectMapper objectMapper;

    public Mono<String> getValue(String key) {
        return redisOps.opsForValue().get(key).map(String::valueOf);
    }

    public Mono<Boolean> setValue(String key, Object value) {
        return redisOps.opsForValue().set(key, value);
    }

    public Mono<Boolean> setValue(String key, Object value, Duration duration) {
        return redisOps.opsForValue().set(key, value)
                .then(redisOps.expire(key, duration));
    }

    public <T> Mono<T> getCacheValueGeneric(String key, Class<T> clazz) {
        return redisOps.opsForValue().get(key)
                .switchIfEmpty(Mono.error(new CustomException(CommonErrorCode.NOT_FOUND_DATA)))
                .flatMap(value -> Mono.just(objectMapper.convertValue(value, clazz)));
    }
}
