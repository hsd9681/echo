package com.echo.echo.domain.friend;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.friend.dto.FriendshipResponseDto;
import com.echo.echo.domain.friend.dto.RequestFriendResponseDto;
import com.echo.echo.domain.friend.entity.Friendship;
import com.echo.echo.domain.friend.entity.RequestFriend;
import com.echo.echo.domain.friend.error.FriendErrorCode;
import com.echo.echo.domain.friend.repository.FriendshipRepository;
import com.echo.echo.domain.friend.repository.RequestFriendRepository;
import com.echo.echo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FriendService는 친구 관련 비즈니스 로직을 처리
 */

@Service
@RequiredArgsConstructor
public class FriendService {

    private final RequestFriendRepository requestFriendRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public Mono<RequestFriendResponseDto> sendFriendRequest(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            return Mono.error(new CustomException(FriendErrorCode.INVALID_USER_ID));
        }
        return validateUserId(fromUserId)
            .then(validateUserId(toUserId))
            .then(checkExistingRequests(fromUserId, toUserId))
            .flatMap(existingRequestStatus -> {
                if (existingRequestStatus == RequestStatus.ALREADY_SENT) {
                    return Mono.error(new CustomException(FriendErrorCode.REQUEST_ALREADY_SENT));
                } else if (existingRequestStatus == RequestStatus.ALREADY_FRIENDS) {
                    return Mono.error(new CustomException(FriendErrorCode.ALREADY_FRIENDS));
                }
                return saveFriendRequest(fromUserId, toUserId);
            });
    }

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


    private enum RequestStatus {
        NONE,
        ALREADY_SENT,
        ALREADY_FRIENDS
    }

    private Mono<RequestStatus> checkExistingRequests(Long fromUserId, Long toUserId) {
        return requestFriendRepository.findAllByToUserIdAndStatus(toUserId, RequestFriend.Status.PENDING)
            .filter(request -> request.getFromUserId().equals(fromUserId))
            .hasElements()
            .flatMap(hasPendingRequest -> {
                if (hasPendingRequest) {
                    return Mono.just(RequestStatus.ALREADY_SENT);
                }
                return friendshipRepository.existsByUserIdAndFriendId(fromUserId, toUserId)
                    .flatMap(hasFriendship -> {
                        if (hasFriendship) {
                            return Mono.just(RequestStatus.ALREADY_FRIENDS);
                        }
                        return friendshipRepository.existsByUserIdAndFriendId(toUserId, fromUserId)
                            .map(reverseFriendship -> reverseFriendship ? RequestStatus.ALREADY_FRIENDS : RequestStatus.NONE);
                    });
            });
    }

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

    public Mono<FriendshipResponseDto> acceptFriendRequest(Long requestId) {
        return processFriendRequest(requestId, RequestFriend.Status.ACCEPTED)
            .switchIfEmpty(Mono.error(new CustomException(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND)))
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

    public Mono<Void> rejectFriendRequest(Long requestId) {
        return processFriendRequest(requestId, RequestFriend.Status.REJECTED)
            .switchIfEmpty(Mono.error(new CustomException(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND)))
            .then();
    }

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

    private Mono<Friendship> saveFriendship(Long userId, Long friendId) {
        return friendshipRepository.save(Friendship.builder()
            .userId(userId)
            .friendId(friendId)
            .build());
    }

    public Flux<RequestFriendResponseDto> getFriendRequests(Long userId) {
        return requestFriendRepository.findAllByToUserIdAndStatus(userId, RequestFriend.Status.PENDING)
            .filterWhen(request -> friendshipRepository.existsByUserIdAndFriendId(request.getFromUserId(), request.getToUserId()).map(exists -> !exists))
            .switchIfEmpty(Mono.error(new CustomException(FriendErrorCode.NO_FRIEND_REQUESTS)))
            .map(request -> RequestFriendResponseDto.builder()
                .fromUserId(request.getFromUserId())
                .toUserId(request.getToUserId())
                .status(request.getStatus().name())
                .build());
    }

    public Flux<FriendshipResponseDto> getFriends(Long userId) {
        return friendshipRepository.findAllByUserId(userId)
            .map(this::toFriendshipResponseDto)
            .switchIfEmpty(Mono.error(new CustomException(FriendErrorCode.NO_FRIENDS_FOUND)));
    }

    private Mono<Void> validateUserId(Long userId) {
        if (userId == null) {
            return Mono.error(new CustomException(FriendErrorCode.INVALID_USER_ID));
        }
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new CustomException(FriendErrorCode.USER_NOT_FOUND)))
            .then();
    }

    private FriendshipResponseDto toFriendshipResponseDto(Friendship friendship) {
        return FriendshipResponseDto.builder()
            .userId(friendship.getUserId())
            .friendId(friendship.getFriendId())
            .build();
    }
}
