package com.echo.echo.domain.thread;

import com.echo.echo.domain.space.SpaceService;
import com.echo.echo.domain.thread.dto.ThreadMessageRequestDto;
import com.echo.echo.domain.thread.dto.ThreadMessageResponseDto;
import com.echo.echo.domain.thread.dto.ThreadResponseDto;
import com.echo.echo.domain.thread.entity.Thread;
import com.echo.echo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class ThreadFacade {

    private final SpaceService spaceService;
    private final ThreadService threadService;

    public Mono<ThreadResponseDto> createThread(Long spaceId, User user, String textId) {
        return spaceService.checkSpaceMember(spaceId, user.getId())
                .then(threadService.createThread(user, textId));
    }

    public Mono<ThreadMessageResponseDto> saveThreadMessage(Long spaceId, User user, Long threadId, ThreadMessageRequestDto req) {
        return spaceService.checkSpaceMember(spaceId, user.getId())
                .then(threadService.saveThreadMessage(user, threadId, req));
    }

    public Mono<ThreadResponseDto> updateThreadStatus(Long spaceId, User user, Long threadId, Thread.Status status) {
        return spaceService.checkSpaceMember(spaceId, user.getId())
                .then(threadService.updateThreadStatus(threadId, status));
    }

    public Flux<ThreadResponseDto> getThreads(Long spaceId, User user, String textId) {
        return spaceService.checkSpaceMember(spaceId, user.getId())
                .thenMany(threadService.getThreadsByTextId(textId));
    }

    public Flux<ThreadMessageResponseDto> getThreadMessages(Long spaceId, User user, Long threadId) {
        return spaceService.checkSpaceMember(spaceId, user.getId())
                .thenMany(threadService.getThreadMessages(threadId));
    }

    public Mono<ThreadMessageResponseDto> updateThreadMessage(User user, Long threadMessageId, ThreadMessageRequestDto req) {
        return threadService.updateThreadMessage(user, threadMessageId, req);
    }

    public Mono<Void> deleteThread(Long spaceId, User user, Long threadId) {
        return spaceService.checkSpaceMember(spaceId, user.getId())
                .then(threadService.deleteThread(threadId));
    }
}
