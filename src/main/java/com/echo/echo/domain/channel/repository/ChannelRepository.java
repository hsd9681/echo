package com.echo.echo.domain.channel.repository;

import com.echo.echo.domain.channel.entity.Channel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ChannelRepository extends ReactiveCrudRepository<Channel, Long> {

}
