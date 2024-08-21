package com.echo.echo.domain.thread;

import com.echo.echo.common.util.ObjectStringConverter;
import com.echo.echo.domain.thread.dto.ThreadMessageRequestDto;
import com.echo.echo.domain.thread.service.ThreadWebSocketService;
import com.echo.echo.domain.user.entity.User;
import com.echo.echo.security.jwt.JwtProvider;
import com.echo.echo.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ThreadWebSocketHandler implements WebSocketHandler {

    private final JwtProvider jwtProvider;
    private final ThreadFacade threadFacade;
    private final ThreadWebSocketService threadWebsocketService;
    private final ObjectStringConverter objectStringConverter;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String query = session.getHandshakeInfo().getUri().getQuery();
        Long spaceId = Long.valueOf(getParam(query, "spaceId"));
        Long threadId = Long.valueOf(getParam(query, "threadId"));
        String token = getParam(query, "token");

        String username = jwtProvider.getNickName(token);
        Long userId = jwtProvider.getUserId(token);

        User user = User.builder()
                .id(userId)
                .nickname(username)
                .build();

        Mono<Void> receive = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .filter(message -> !"$p&ing".equals(message))
                .switchIfEmpty(Mono.empty())
                .flatMap(message -> objectStringConverter.stringToObject(message, ThreadMessageRequestDto.class))
                .flatMap(req -> threadFacade.saveThreadMessage(spaceId, user, threadId, req))
                .flatMap(threadWebsocketService::publishMessage)
                .then()
                .doFinally(s -> closeSession(session))
                .doOnError(s -> closeSession(session));

        Mono<Void> send = threadWebsocketService.sendMessage(session, threadId);

        return Mono.when(receive, send).then();
    }

    private void closeSession(WebSocketSession session) {
        session.close().subscribe();
    }

    private String getParam(String query, String key) {
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue[0].equals(key)){
                return keyValue[1];
            }
        }
        return "";
    }

    /**
     * 토큰에 있는 유저에 대한 정보를 가져온다
     */
    private Mono<User> getUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> ((UserPrincipal) context).getUser());
    }
}
