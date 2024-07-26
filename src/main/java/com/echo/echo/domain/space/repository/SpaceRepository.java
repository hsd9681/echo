package com.echo.echo.domain.space.repository;

import com.echo.echo.domain.space.entity.Space;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SpaceRepository extends ReactiveCrudRepository<Space, Long> {
    Mono<Space> findByUuid(String uuid);
}
