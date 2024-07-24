package com.echo.echo.domain.channel.repository;

import com.echo.echo.domain.channel.entity.Channel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ChannelRepository extends ReactiveCrudRepository<Channel, Long> {
    Flux<Channel> findByChannelType(String channelType);
    Mono<Channel> findByChannelId(Long channelId);
}
