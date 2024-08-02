package com.echo.echo.domain.friend;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.friend.dto.FriendshipResponseDto;
import com.echo.echo.domain.friend.dto.RequestFriendResponseDto;
import com.echo.echo.domain.friend.error.FriendErrorCode;
import com.echo.echo.domain.user.UserService;
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
    private final UserService userService;

    public Mono<Void> sendFriendRequest(Long fromUserId, Long toUserId) {
        return validateUserIds(fromUserId, toUserId)
            .flatMap(valid -> friendService.sendFriendRequest(fromUserId, toUserId))
            .then();
    }

    public Mono<Void> acceptFriendRequest(Long requestId) {
        return friendService.acceptFriendRequest(requestId).then();
    }

    public Mono<Void> rejectFriendRequest(Long requestId) {
        return friendService.rejectFriendRequest(requestId).then();
    }

    public Mono<Void> deleteFriend(Long userId, Long friendId) {
        return validateUserIds(userId, friendId)
            .flatMap(valid -> friendService.deleteFriend(userId, friendId))
            .then();
    }

    public Flux<RequestFriendResponseDto> getFriendRequests(Long userId) {
        return validateUserIds(userId)
            .flatMapMany(valid -> friendService.getFriendRequests(userId));
    }

    public Flux<FriendshipResponseDto> getFriends(Long userId) {
        return validateUserIds(userId)
            .flatMapMany(valid -> friendService.getFriends(userId));
    }

    private Mono<Boolean> validateUserIds(Long... userIds) {
        return Flux.fromArray(userIds)
            .flatMap(userId -> userService.findById(userId)
                .hasElement()
                .flatMap(exists -> exists ? Mono.just(true) : Mono.error(new CustomException(FriendErrorCode.USER_NOT_FOUND)))
            )
            .reduce(Boolean::logicalAnd)
            .switchIfEmpty(Mono.just(true));
    }

}
