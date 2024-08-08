package com.echo.echo.config;

import com.echo.echo.common.redis.RedisConst;
import com.echo.echo.common.redis.RedisListener;
import com.echo.echo.domain.user.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Configuration
@EnableRedisRepositories(basePackageClasses = RefreshToken.class)
@RequiredArgsConstructor
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Object> redisOperations(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Object> context = builder
                .value(serializer)
                .hashValue(serializer)
                .hashKey(serializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisMessageListenerContainer redisMessageListenerContainer(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisMessageListenerContainer(factory);
    }

    @Bean
    ApplicationRunner applicationRunner(RedisListener redisListener) {
        return args -> {
            List<String> topics = List.of(RedisConst.TEXT_CHANNEL_PREFIX);  // Redis에 퍼블리싱된 채널 중 어떤 걸 감시할 지 설정하는 부분
            Flux.fromIterable(topics)
                    .flatMap(redisListener::addTopic)
                    .thenMany(redisListener.getMessageFlux())
                    .doOnSubscribe(subscription -> log.info("Redis Listener 작동"))
                    .doOnError(throwable -> log.error("Redis topic Listener 오류", throwable))
                    .doFinally(signalType -> log.info("Listener 중지. Signal Type: {}", signalType))
                    .flatMap(message -> {
                        String topic = message.getChannel();
                        String body = message.getMessage();
                        log.info("[{}] topic으로 받은 메시지: {}", topic, body);

                        return redisListener.handleMessage(topic, body);
                    })
                    .subscribe();
        };
    }
}
