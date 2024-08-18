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

/**
 * FriendController는 친구 관련 API 요청을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/users/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendFacade friendFacade;

    /**
     * 친구 요청을 보냅니다.
     *
     * @param userPrincipal 현재 인증된 사용자 정보
     * @param toUserId 요청을 받을 사용자의 ID
     * @return 성공 메시지 응답
     */
    @PostMapping("/request/{toUserId}")
    public Mono<ResponseEntity<String>> sendFriendRequest(
        @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long toUserId) {
        Long fromUserId = userPrincipal.getUser().getId();
        return friendFacade.sendFriendRequest(fromUserId, toUserId)
            .then(Mono.just(ResponseEntity.ok(FriendSuccessCode.REQUEST_SENT.getMsg())));
    }

    /**
     * 친구 요청을 수락합니다.
     *
     * @param requestId 수락할 친구 요청의 ID
     * @return 성공 메시지 응답
     */
    @PostMapping("/accept/{requestId}")
    public Mono<ResponseEntity<String>> acceptFriendRequest(@PathVariable Long requestId) {
        return friendFacade.acceptFriendRequest(requestId)
            .then(Mono.just(ResponseEntity.ok(FriendSuccessCode.REQUEST_ACCEPTED.getMsg())));
    }

    /**
     * 친구 요청을 거부합니다.
     *
     * @param requestId 거부할 친구 요청의 ID
     * @return 성공 메시지 응답
     */
    @PostMapping("/reject/{requestId}")
    public Mono<ResponseEntity<String>> rejectFriendRequest(@PathVariable Long requestId) {
        return friendFacade.rejectFriendRequest(requestId)
            .then(Mono.just(ResponseEntity.ok(FriendSuccessCode.REQUEST_REJECTED.getMsg())));
    }

    /**
     * 친구를 삭제합니다.
     *
     * @param userPrincipal 현재 인증된 사용자 정보
     * @param friendId 삭제할 친구의 ID
     * @return 성공 메시지 응답
     */
    @DeleteMapping("/{friendId}")
    public Mono<ResponseEntity<String>> deleteFriend(
        @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long friendId) {
        Long userId = userPrincipal.getUser().getId();
        return friendFacade.deleteFriend(userId, friendId)
            .then(Mono.just(ResponseEntity.ok(FriendSuccessCode.FRIEND_DELETED.getMsg())));
    }

    /**
     * 사용자가 받은 친구 요청 목록을 조회합니다.
     *
     * @param userPrincipal 현재 인증된 사용자 정보
     * @return 친구 요청 목록 응답
     */
    @GetMapping("/requests")
    public Flux<RequestFriendResponseDto> getFriendRequests(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        return friendFacade.getFriendRequests(userId);
    }

    /**
     * 사용자의 친구 목록을 조회합니다.
     *
     * @param userPrincipal 현재 인증된 사용자 정보
     * @return 친구 목록 응답
     */
    @GetMapping
    public Flux<FriendshipResponseDto> getFriends(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        return friendFacade.getFriends(userId);
    }

}
