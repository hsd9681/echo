package com.echo.echo.domain.dm.repository;

import com.echo.echo.domain.dm.entity.Dm;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface DmRepository extends ReactiveMongoRepository<Dm, String> {

    Flux<Dm> findAllBySenderIdOrReceiverId(Long senderId, Long ReceiverId);

}
