package com.echo.echo.domain.notification;

import com.echo.echo.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> sseConnect(@AuthenticationPrincipal UserPrincipal user) {
        return notificationService.connect(user.getId());
    }

    @GetMapping("/sse/{message}")
    public Mono<Void> sendMessage(@AuthenticationPrincipal UserPrincipal user,
                                    @PathVariable String message) {
        return notificationService.personalSend(user.getId(), message);
    }
}
