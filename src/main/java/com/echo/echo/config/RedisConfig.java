package com.echo.echo.config;

import com.echo.echo.common.redis.RedisConst;
import com.echo.echo.common.redis.RedisListener;
import com.echo.echo.domain.user.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import reactor.core.publisher.Flux;

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
    public ReactiveRedisMessageListenerContainer redisMessageListenerContainer(ReactiveRedisConnectionFactory factory, RedisListener redisListener) {
        ReactiveRedisMessageListenerContainer container = new ReactiveRedisMessageListenerContainer(factory);
        Flux<ChannelTopic> topics = Flux.fromArray(RedisConst.values())
                .map(RedisConst::name)
                .map(ChannelTopic::new);

        topics.flatMap(topic -> container.receive(topic)
                .flatMap(message -> {
                    RedisConst channel = RedisConst.valueOf(message.getChannel()) ;
                    String body = message.getMessage();
                    log.debug("[{}] topic으로 받은 메시지: {}", channel, body);

                    return redisListener.handleMessage(channel, body);
                }))
                .doFinally(signalType -> {
                    container.destroy();
                })
                .subscribe();

        return container;
    }
}
