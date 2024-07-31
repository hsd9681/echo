package com.echo.echo.domain.friend.repository;

import com.echo.echo.domain.friend.entity.Friendship;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FriendshipRepository extends ReactiveCrudRepository<Friendship, Long> {
    Flux<Friendship> findAllByUserId(Long userId);
    Mono<Boolean> existsByUserIdAndFriendId(Long userId, Long friendId);
    Mono<Void> deleteByUserIdAndFriendId(Long userId, Long friendId);
}
