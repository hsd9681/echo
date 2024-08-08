package com.echo.echo.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class ObjectStringConverter {

    private final ObjectMapper mapper;

    public  <T> Mono<String> objectToString(T object) {
        return Mono.fromCallable(() -> mapper.writeValueAsString(object)).log()
                .doOnError(throwable -> log.error("[{}] 객체를 문자열로 변환중 오류 발생.", object));
    }

    public <T> Mono<T> stringToObject(String payload, Class<T> clazz) {
        return Mono.fromCallable(() -> mapper.readValue(payload, clazz))
                .doOnError(throwable -> log.error("[{}] 문자열을 '{}' 객체로 변환중 오류 발생.", payload, clazz.getSimpleName()));
    }
}
