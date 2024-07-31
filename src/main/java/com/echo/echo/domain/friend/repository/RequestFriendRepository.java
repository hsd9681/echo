package com.echo.echo.domain.friend.repository;

import com.echo.echo.domain.friend.entity.RequestFriend;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RequestFriendRepository extends ReactiveCrudRepository<RequestFriend, Long> {
    Flux<RequestFriend> findAllByToUserIdAndStatus(Long toUserId, RequestFriend.Status status);
}
