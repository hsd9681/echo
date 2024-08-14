package com.echo.echo.domain.thread.service;

import com.echo.echo.common.redis.RedisConst;
import com.echo.echo.common.redis.RedisPublisher;
import com.echo.echo.common.util.ObjectStringConverter;
import com.echo.echo.domain.thread.dto.ThreadMessageResponseDto;
import com.echo.echo.domain.thread.repository.ThreadWebsocketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RequiredArgsConstructor
@Service
public class ThreadWebsocketService {

    private final ThreadWebsocketRepository threadWebsocketRepository;
    private final ObjectStringConverter objectStringConverter;
    private final RedisPublisher redisPublisher;

    /**
     * 각 스레드에게 메시지를 내보냅니다.
     * @param threadMessage 전송할 메시지 내용
     */
    public void emitMessage(ThreadMessageResponseDto threadMessage) {
        threadWebsocketRepository.getSinks(threadMessage.getThreadId())
                .tryEmitNext(threadMessage);
    }

    /**
     * 실제 세션에게 메시지를 전송한다.
     * @param session 웹소켓 세션
     * @param threadId 스레드 고유 번호
     */
    public Mono<Void> sendMessage(WebSocketSession session, Long threadId) {
        Sinks.Many<ThreadMessageResponseDto> threadMessageSinks = threadWebsocketRepository.getSinks(threadId);
        return session.send(
                threadMessageSinks.asFlux()
                        .flatMap(objectStringConverter::objectToString)
                        .map(session::textMessage)
                )
                .then();
    }
}
