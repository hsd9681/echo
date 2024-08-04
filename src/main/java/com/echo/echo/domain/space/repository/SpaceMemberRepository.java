package com.echo.echo.domain.space.repository;

import com.echo.echo.domain.space.entity.SpaceMember;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * SpaceMemberRepository는 스페이스 멤버 데이터를 처리하는 리포지토리 인터페이스
 */

public interface SpaceMemberRepository extends ReactiveCrudRepository<SpaceMember, Long> {
    Mono<SpaceMember> findByUserIdAndSpaceId(Long userId, Long spaceId);
    Flux<SpaceMember> findAllByUserId(Long userId);
    Mono<Void> deleteBySpaceId(Long spaceId);
    Flux<SpaceMember> findAllBySpaceId(Long spaceId);

}
