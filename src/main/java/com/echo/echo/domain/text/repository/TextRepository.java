package com.echo.echo.domain.text.repository;

import com.echo.echo.domain.text.entity.Text;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TextRepository extends ReactiveMongoRepository<Text, String> {

    Flux<Text> findAllByChannelId(Long channelId);
}
