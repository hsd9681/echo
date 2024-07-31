package com.echo.echo.domain.friend;

import com.echo.echo.domain.friend.dto.FriendshipResponseDto;
import com.echo.echo.domain.friend.dto.RequestFriendResponseDto;
import com.echo.echo.domain.friend.error.FriendSuccessCode;
import com.echo.echo.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendFacade friendFacade;

    @PostMapping("/request/{toUserId}")
    public Mono<ResponseEntity<String>> sendFriendRequest(
        @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long toUserId) {
        Long fromUserId = userPrincipal.getUser().getId();
        return friendFacade.sendFriendRequest(fromUserId, toUserId)
            .then(Mono.just(ResponseEntity.ok(FriendSuccessCode.REQUEST_SENT.getMsg())));
    }

    @PostMapping("/accept/{requestId}")
    public Mono<ResponseEntity<String>> acceptFriendRequest(@PathVariable Long requestId) {
        return friendFacade.acceptFriendRequest(requestId)
            .then(Mono.just(ResponseEntity.ok(FriendSuccessCode.REQUEST_ACCEPTED.getMsg())));
    }

    @PostMapping("/reject/{requestId}")
    public Mono<ResponseEntity<String>> rejectFriendRequest(@PathVariable Long requestId) {
        return friendFacade.rejectFriendRequest(requestId)
            .then(Mono.just(ResponseEntity.ok(FriendSuccessCode.REQUEST_REJECTED.getMsg())));
    }

    @DeleteMapping("/{friendId}")
    public Mono<ResponseEntity<String>> deleteFriend(
        @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long friendId) {
        Long userId = userPrincipal.getUser().getId();
        return friendFacade.deleteFriend(userId, friendId)
            .then(Mono.just(ResponseEntity.ok(FriendSuccessCode.FRIEND_DELETED.getMsg())));
    }

    @GetMapping("/requests")
    public Flux<RequestFriendResponseDto> getFriendRequests(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        return friendFacade.getFriendRequests(userId);
    }

    @GetMapping
    public Flux<FriendshipResponseDto> getFriends(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        return friendFacade.getFriends(userId);
    }
}
