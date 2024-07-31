package com.echo.echo.domain.friend;

import com.echo.echo.domain.friend.dto.FriendshipResponseDto;
import com.echo.echo.domain.friend.dto.RequestFriendResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FriendFacade는 FriendService를 통해 친구 관련 비즈니스 로직을 처리
 */

@RequiredArgsConstructor
@Component
public class FriendFacade {

    private final FriendService friendService;

    public Mono<Void> sendFriendRequest(Long fromUserId, Long toUserId) {
        return friendService.sendFriendRequest(fromUserId, toUserId).then();
    }

    public Mono<Void> acceptFriendRequest(Long requestId) {
        return friendService.acceptFriendRequest(requestId).then();
    }

    public Mono<Void> rejectFriendRequest(Long requestId) {
        return friendService.rejectFriendRequest(requestId).then();
    }

    public Mono<Void> deleteFriend(Long userId, Long friendId) {
        return friendService.deleteFriend(userId, friendId).then();
    }

    public Flux<RequestFriendResponseDto> getFriendRequests(Long userId) {
        return friendService.getFriendRequests(userId);
    }

    public Flux<FriendshipResponseDto> getFriends(Long userId) {
        return friendService.getFriends(userId);
    }
}
