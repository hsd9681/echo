package com.echo.echo.domain.channel.repository;

import com.echo.echo.domain.channel.entity.Channel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * ChannelRepository는 채널 데이터를 처리하는 리포지토리 인터페이스
 */
public interface ChannelRepository extends ReactiveCrudRepository<Channel, Long> {
    Flux<Channel> findBySpaceId(Long spaceId);
}
