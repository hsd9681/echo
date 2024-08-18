package com.echo.echo.domain.text.repository;

import com.echo.echo.domain.text.entity.Text;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TextRepository extends ReactiveMongoRepository<Text, String> {

    Flux<Text> findAllByChannelId(Long channelId);

    Flux<Text> findAllByDmId(String dmId);

}
