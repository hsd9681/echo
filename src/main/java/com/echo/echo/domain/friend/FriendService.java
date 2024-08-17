package com.echo.echo.domain.friend;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.friend.dto.FriendshipResponseDto;
import com.echo.echo.domain.friend.dto.RequestFriendResponseDto;
import com.echo.echo.domain.friend.entity.Friendship;
import com.echo.echo.domain.friend.entity.RequestFriend;
import com.echo.echo.domain.friend.error.FriendErrorCode;
import com.echo.echo.domain.friend.repository.FriendshipRepository;
import com.echo.echo.domain.friend.repository.RequestFriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FriendService는 친구 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */

@Service
@RequiredArgsConstructor
public class FriendService {

    private final RequestFriendRepository requestFriendRepository;
    private final FriendshipRepository friendshipRepository;

    /**
     * 친구 요청을 생성합니다.
     *
     * @param fromUserId 요청을 보낸 사용자 ID
     * @param toUserId 요청을 받을 사용자 ID
     * @return 생성된 친구 요청 응답 DTO
     */
    public Mono<RequestFriendResponseDto> sendFriendRequest(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            return Mono.error(new CustomException(FriendErrorCode.INVALID_USER_ID));
        }
        return checkExistingRequests(fromUserId, toUserId)
            .flatMap(existingRequestStatus -> {
                if (existingRequestStatus == RequestFriend.Status.PENDING) {
                    return Mono.error(new CustomException(FriendErrorCode.REQUEST_ALREADY_SENT));
                } else if (existingRequestStatus == RequestFriend.Status.ACCEPTED) {
                    return Mono.error(new CustomException(FriendErrorCode.ALREADY_FRIENDS));
                }
                return saveFriendRequest(fromUserId, toUserId);
            });
    }

    /**
     * 친구를 삭제합니다.
     *
     * @param userId 사용자의 ID
     * @param friendId 삭제할 친구의 ID
     * @return 삭제 작업 처리
     */
    public Mono<Void> deleteFriend(Long userId, Long friendId) {
        return friendshipRepository.existsByUserIdAndFriendId(userId, friendId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new CustomException(FriendErrorCode.NOT_FRIENDS));
                }
                return friendshipRepository.deleteByUserIdAndFriendId(userId, friendId)
                    .then(friendshipRepository.deleteByUserIdAndFriendId(friendId, userId))
                    .then();
            });
    }

    /**
     * 친구 요청 상태를 확인합니다.
     *
     * @param fromUserId 요청을 보낸 사용자 ID
     * @param toUserId 요청을 받을 사용자 ID
     * @return 요청 상태를 나타내는 Mono 객체
     */
    private Mono<RequestFriend.Status> checkExistingRequests(Long fromUserId, Long toUserId) {
        return requestFriendRepository.findAllByToUserIdAndStatus(toUserId, RequestFriend.Status.PENDING)
            .filter(request -> request.getFromUserId().equals(fromUserId))
            .hasElements()
            .flatMap(hasPendingRequest -> {
                if (hasPendingRequest) {
                    return Mono.just(RequestFriend.Status.PENDING);
                }
                return friendshipRepository.existsByUserIdAndFriendId(fromUserId, toUserId)
                    .flatMap(hasFriendship -> {
                        if (hasFriendship) {
                            return Mono.just(RequestFriend.Status.ACCEPTED);
                        }
                        return friendshipRepository.existsByUserIdAndFriendId(toUserId, fromUserId)
                            .map(reverseFriendship -> reverseFriendship ? RequestFriend.Status.ACCEPTED : RequestFriend.Status.REJECTED);
                    });
            });
    }

    /**
     * 친구 요청을 저장합니다.
     *
     * @param fromUserId 요청을 보낸 사용자 ID
     * @param toUserId 요청을 받을 사용자 ID
     * @return 생성된 친구 요청 응답 DTO
     */
    private Mono<RequestFriendResponseDto> saveFriendRequest(Long fromUserId, Long toUserId) {
        RequestFriend request = RequestFriend.builder()
            .fromUserId(fromUserId)
            .toUserId(toUserId)
            .status(RequestFriend.Status.PENDING)
            .build();
        return requestFriendRepository.save(request)
            .map(savedRequest -> RequestFriendResponseDto.builder()
                .fromUserId(savedRequest.getFromUserId())
                .toUserId(savedRequest.getToUserId())
                .status(savedRequest.getStatus().name())
                .build());
    }

    /**
     * 친구 요청을 수락합니다.
     *
     * @param requestId 수락할 친구 요청의 ID
     * @return 수락된 친구 요청의 응답 DTO
     */
    public Mono<FriendshipResponseDto> acceptFriendRequest(Long requestId) {
        return processFriendRequest(requestId, RequestFriend.Status.ACCEPTED)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND))))
            .flatMap(request -> {
                Mono<Friendship> saveFriendship1 = saveFriendship(request.getFromUserId(), request.getToUserId());
                Mono<Friendship> saveFriendship2 = saveFriendship(request.getToUserId(), request.getFromUserId());
                return Mono.zip(saveFriendship1, saveFriendship2)
                    .then(Mono.just(FriendshipResponseDto.builder()
                        .userId(request.getFromUserId())
                        .friendId(request.getToUserId())
                        .build()));
            });
    }

    /**
     * 친구 요청을 거부합니다.
     *
     * @param requestId 거부할 친구 요청의 ID
     * @return 거부된 친구 요청의 처리 결과
     */
    public Mono<Void> rejectFriendRequest(Long requestId) {
        return processFriendRequest(requestId, RequestFriend.Status.REJECTED)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND))))
            .then();
    }

    /**
     * 친구 요청을 처리합니다.
     *
     * @param requestId 처리할 친구 요청의 ID
     * @param newStatus 요청의 새로운 상태
     * @return 처리된 친구 요청 엔티티
     */
    private Mono<RequestFriend> processFriendRequest(Long requestId, RequestFriend.Status newStatus) {
        return requestFriendRepository.findById(requestId)
            .flatMap(request -> {
                if (request.getStatus() != RequestFriend.Status.PENDING) {
                    return Mono.error(new CustomException(FriendErrorCode.FRIEND_REQUEST_ALREADY_PROCESSED));
                }
                RequestFriend updatedRequest = request.changeStatus(newStatus);
                return requestFriendRepository.save(updatedRequest);
            });
    }

    /**
     * 친구 관계를 저장합니다.
     *
     * @param userId 사용자의 ID
     * @param friendId 친구의 ID
     * @return 저장된 친구 관계 엔티티
     */
    private Mono<Friendship> saveFriendship(Long userId, Long friendId) {
        return friendshipRepository.save(Friendship.builder()
            .userId(userId)
            .friendId(friendId)
            .build());
    }

    /**
     * 사용자가 받은 친구 요청 목록을 조회합니다.
     *
     * @param userId 사용자의 ID
     * @return 친구 요청 목록 응답 DTO
     */
    public Flux<RequestFriendResponseDto> getFriendRequests(Long userId) {
        return requestFriendRepository.findAllByToUserIdAndStatus(userId, RequestFriend.Status.PENDING)
            .filterWhen(request -> friendshipRepository.existsByUserIdAndFriendId(request.getFromUserId(), request.getToUserId()).map(exists -> !exists))
            .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(FriendErrorCode.NO_FRIEND_REQUESTS))))
            .map(request -> RequestFriendResponseDto.builder()
                .fromUserId(request.getFromUserId())
                .toUserId(request.getToUserId())
                .status(request.getStatus().name())
                .build());
    }

    /**
     * 사용자의 친구 목록을 조회합니다.
     *
     * @param userId 사용자의 ID
     * @return 친구 목록 응답 DTO
     */
    public Flux<Friendship> getFriends(Long userId) {
        return friendshipRepository.findAllByUserId(userId)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(FriendErrorCode.NO_FRIENDS_FOUND))));
    }
}
