package com.echo.echo.domain.notification;

import com.echo.echo.domain.notification.dto.NotificationResponseDto;
import com.echo.echo.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NotificationController {

    private final SseProcessor sseProcessor;
    private final NotificationFacade notificationFacade;

    @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationResponseDto>> sseConnect(@AuthenticationPrincipal UserPrincipal user) {
        return sseProcessor.connect(user.getId());
    }

    @DeleteMapping("{channelId}")
    public Mono<Void> readNotification(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                       @PathVariable("channelId") Long channelId) {
        return notificationFacade.deleteNotification(userPrincipal.getId(), channelId);
    }

}
