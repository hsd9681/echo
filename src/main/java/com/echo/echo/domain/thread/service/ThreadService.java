package com.echo.echo.domain.thread.service;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.thread.dto.ThreadMessageRequestDto;
import com.echo.echo.domain.thread.dto.ThreadMessageResponseDto;
import com.echo.echo.domain.thread.dto.ThreadResponseDto;
import com.echo.echo.domain.thread.entity.ThreadMessage;
import com.echo.echo.domain.thread.entity.Thread;
import com.echo.echo.domain.thread.error.ThreadErrorCode;
import com.echo.echo.domain.thread.repository.ThreadRepository;
import com.echo.echo.domain.thread.repository.ThreadMessageRepository;
import com.echo.echo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ThreadService {

    private final ThreadMessageRepository threadMessageRepository;
    private final ThreadRepository threadRepository;

    /**
     * 해당 채팅 메시지에 대한 스레드를 생성한다.
     * @param user 생성한 유저 정보
     * @param textId 채팅 메시지 고유 아이디
     */
    public Mono<ThreadResponseDto> createThread(User user, Long channelId, String textId) {
        return threadRepository.existsByTextId(textId)
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(ThreadErrorCode.ALREADY_EXISTS_THREAD))))
                .flatMap(unused -> threadRepository.save(
                        Thread.builder()
                                .channelId(channelId)
                                .textId(textId)
                                .creatorId(user.getId())
                                .build()
                ))
                .map(thread -> new ThreadResponseDto(thread, user.getNickname()));
    }

    /**
     * 해당 스레드에 스레드 메시지를 저장한다.
     * @param user 보낸 유저 정보
     * @param threadId 채팅 메시지 고유 아이디
     * @param req 스레드 메시지 정보
     */
    public Mono<ThreadMessageResponseDto> saveThreadMessage(User user, Long threadId, ThreadMessageRequestDto req) {
        return threadRepository.existsById(threadId)
                .filter(exists -> exists)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(ThreadErrorCode.NOT_FOUND_THREAD))))
                .flatMap(unused -> threadMessageRepository.save(
                        ThreadMessage.builder()
                                .threadId(threadId)
                                .authorId(user.getId())
                                .content(req.getContent())
                                .build()
                ))
                .map(threadMessage -> new ThreadMessageResponseDto(threadMessage, user.getNickname()));
    }

    /**
     * 해당하는 스레드의 모든 메시지를 가져온다.
     * @param threadId 스레드 고유 아이디
     */
    public Flux<ThreadMessageResponseDto> getThreadMessages(Long threadId) {
        return threadMessageRepository.findAllByThreadId(threadId)
                .map(ThreadMessageResponseDto::new);
    }

    /**
     * 해당하는 스레드의 메시지를 변경한다.
     * @param threadMessageId 스레드 메시지 고유 아이디
     */
    public Mono<ThreadMessageResponseDto> updateThreadMessage(User user, Long threadMessageId, ThreadMessageRequestDto req) {
        return findThreadMessageById(threadMessageId)
                .filter(threadMessage -> Objects.equals(user.getId(), threadMessage.getAuthorId()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(ThreadErrorCode.NOT_THREAD_MESSAGE_AUTHOR))))
                .map(threadMessage -> {
                    threadMessage.updateMessage(req.getContent());
                    return threadMessage;
                })
                .map(ThreadMessageResponseDto::new);
    }

    /**
     * 해당 채팅 메시지에 대한 모든 스레드 정보를 가져온다.
     * @param textId 채팅 메시지 고유 아이디
     */
    public Mono<ThreadResponseDto> getThreadByTextId(String textId) {
        return threadRepository.findByTextIdWithUser(textId)
                .map(ThreadResponseDto::new);
    }

    /**
     * 해당 채널에 대한 모든 스레드 정보를 가져온다.
     * @param channelId 채널 고유 아이디
     */
    public Flux<ThreadResponseDto> getThreadsByChannelId(Long channelId) {
        return threadRepository.findAllByChannelIdWithUser(channelId)
                .map(ThreadResponseDto::new);
    }

    /**
     * 스레드를 활성화/비활성화 한다.
     * @param threadId 스레드 고유 번호
     * @param status 변경할 활성화/비활성화 상태 정보
     */
    public Mono<ThreadResponseDto> updateThreadStatus(Long threadId, Thread.Status status) {
        return findThreadById(threadId)
                .map(thread -> {
                    if (Objects.equals(Thread.Status.OPEN, status)) {
                        thread.openThread();
                    } else {
                        thread.closeThread();
                    }
                    return thread;
                })
                .flatMap(threadRepository::save)
                .map(ThreadResponseDto::new);
    }

    /**
     * 스레드를 삭제한다. 삭제 시 스레드 메시지도 전부 삭제한다.
     * @param threadId 삭제할 스레드 고유 번호
     */
    public Mono<Void> deleteThread(Long threadId) {
        return threadRepository.deleteById(threadId);
    }

    /**
     * 스레드 정보 데이터를 가져온다.
     * @param threadId 가져올 스레드 id
     */
    private Mono<Thread> findThreadById(Long threadId) {
        return threadRepository.findByIdWithUser(threadId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(ThreadErrorCode.NOT_FOUND_THREAD))));
    }

    /**
     * 스레드 데이터를 가져온다.
     * @param threadMessageId 가져올 스레드 메시지 고유 번호
     */
    private Mono<ThreadMessage> findThreadMessageById(Long threadMessageId) {
        return threadMessageRepository.findById(threadMessageId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(ThreadErrorCode.NOT_FOUND_THREAD))));
    }

}
