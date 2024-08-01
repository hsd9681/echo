package com.echo.echo.domain.friend.repository;

import com.echo.echo.domain.friend.entity.Friendship;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FriendshipRepository는 Friendship 엔티티에 대한 데이터 접근을 처리
 */

@Repository
public interface FriendshipRepository extends ReactiveCrudRepository<Friendship, Long> {

    @Query("SELECT * FROM friendship WHERE user_id = :userId")
    Flux<Friendship> findAllByUserId(Long userId);
    Mono<Boolean> existsByUserIdAndFriendId(Long userId, Long friendId);
    Mono<Void> deleteByUserIdAndFriendId(Long userId, Long friendId);
}
