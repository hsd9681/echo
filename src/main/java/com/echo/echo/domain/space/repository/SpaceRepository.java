package com.echo.echo.domain.space.repository;

import com.echo.echo.domain.space.entity.Space;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * SpaceRepository는 스페이스 데이터를 처리하는 리포지토리 인터페이스
 */

public interface SpaceRepository extends ReactiveCrudRepository<Space, Long> {

    Mono<Space> findByUuid(String uuid);

}
