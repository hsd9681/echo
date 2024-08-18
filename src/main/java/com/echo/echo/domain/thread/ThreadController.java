package com.echo.echo.domain.thread;

import com.echo.echo.domain.thread.dto.ThreadMessageRequestDto;
import com.echo.echo.domain.thread.dto.ThreadMessageResponseDto;
import com.echo.echo.domain.thread.dto.ThreadResponseDto;
import com.echo.echo.domain.thread.entity.Thread;
import com.echo.echo.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spaces/{spaceId}/channels/{channelId}/texts/{textId}/threads")
public class ThreadController {

    private final ThreadFacade threadFacade;

    /**
     * 스레드 생성 API
     */
    @PostMapping
    public Mono<ResponseEntity<ThreadResponseDto>> createThread(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                @PathVariable("spaceId") Long spaceId,
                                                                @PathVariable("channelId") Long channelId,
                                                                @PathVariable("textId") String textId) {
        return threadFacade.createThread(spaceId, userPrincipal.getUser(), channelId, textId)
                .map(ResponseEntity::ok);
    }

    /**
     * Thread 목록 출력 API (채널 아이디 기준)
     */
    @GetMapping("/list")
    public Flux<ThreadResponseDto> getThreads(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                              @PathVariable("spaceId") Long spaceId,
                                              @PathVariable("channelId") Long channelId) {
        return threadFacade.getThreads(spaceId, userPrincipal.getUser(), channelId);
    }

    /**
     * Thread 출력 API (텍스트 아이디 기준)
     */
    @GetMapping
    public Mono<ResponseEntity<ThreadResponseDto>> getThread(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                              @PathVariable("spaceId") Long spaceId,
                                              @PathVariable("textId") String textId) {
        return threadFacade.getThread(spaceId, userPrincipal.getUser(), textId)
                .map(ResponseEntity::ok);
    }

    /**
     * Thread 메시지 저장 API
     */
    @PostMapping("/{threadId}")
    public Mono<ResponseEntity<ThreadMessageResponseDto>> saveThreadMessage(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                            @PathVariable("spaceId") Long spaceId,
                                                                            @PathVariable("threadId") Long threadId,
                                                                            @RequestBody ThreadMessageRequestDto req) {
        return threadFacade.saveThreadMessage(spaceId, userPrincipal.getUser(), threadId, req)
                .map(ResponseEntity::ok);
    }

    /**
     * Thread 메시지 출력 API
     */
    @GetMapping("/{threadId}")
    public Flux<ThreadMessageResponseDto> getThreadMessages(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                              @PathVariable("spaceId") Long spaceId,
                                              @PathVariable("threadId") Long threadId) {
        return threadFacade.getThreadMessages(spaceId, userPrincipal.getUser(), threadId);
    }

    /**
     * Thread 활성화 API
     */
    @PutMapping("/{threadId}/open")
    public Mono<ResponseEntity<ThreadResponseDto>> openThreadStatus(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                    @PathVariable("spaceId") Long spaceId,
                                                                    @PathVariable("threadId") Long threadId) {
        return threadFacade.updateThreadStatus(spaceId, userPrincipal.getUser(), threadId, Thread.Status.OPEN)
                .map(ResponseEntity::ok);
    }

    /**
     * Thread 비활성화 API
     */
    @PutMapping("/{threadId}/close")
    public Mono<ResponseEntity<ThreadResponseDto>> closeThreadStatus(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                     @PathVariable("spaceId") Long spaceId,
                                                                     @PathVariable("threadId") Long threadId) {
        return threadFacade.updateThreadStatus(spaceId, userPrincipal.getUser(), threadId, Thread.Status.CLOSE)
                .map(ResponseEntity::ok);
    }

    /**
     * Thread 삭제 API
     */
    @DeleteMapping("/{threadId}")
    public Mono<ResponseEntity<String>> deleteThread(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                     @PathVariable("spaceId") Long spaceId,
                                                     @PathVariable("threadId") Long threadId) {
        return threadFacade.deleteThread(spaceId, userPrincipal.getUser(), threadId)
                .map(data -> ResponseEntity.ok("성공적으로 삭제되었습니다."));
    }

}
