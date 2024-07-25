package com.echo.echo.domain.space.repository;

import com.echo.echo.domain.space.entity.SpaceMember;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SpaceMemberRepository extends ReactiveCrudRepository<SpaceMember, Long> {
    Mono<SpaceMember> findByUserIdAndSpaceId(Long userId, Long spaceId);
}
