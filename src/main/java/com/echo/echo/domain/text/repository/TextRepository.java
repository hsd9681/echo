package com.echo.echo.domain.text.repository;

import com.echo.echo.domain.text.entity.Text;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TextRepository extends ReactiveCrudRepository<Text, Long> {

    Flux<Text> findAllByChannelId(Long channelId);
}
