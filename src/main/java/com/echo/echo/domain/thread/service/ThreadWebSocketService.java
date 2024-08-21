package com.echo.echo.domain.thread.service;

import com.echo.echo.common.redis.RedisConst;
import com.echo.echo.common.redis.RedisPublisher;
import com.echo.echo.common.util.ObjectStringConverter;
import com.echo.echo.domain.thread.dto.ThreadMessageResponseDto;
import com.echo.echo.domain.thread.repository.ThreadWebSocketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RequiredArgsConstructor
@Service
public class ThreadWebSocketService {

    private final ThreadWebSocketRepository threadWebsocketRepository;
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
     * redis에 메시지 발행
     * @param threadMessage 전송할 메시지 내용
     */
    public Mono<Void> publishMessage(ThreadMessageResponseDto threadMessage) {
        return redisPublisher.publish(RedisConst.THREAD.getChannelTopic(), threadMessage);
    }

    /**
     * redis에서 발행된 메시지에 대한 메시지를 내보냅니다.
     * @param threadMessageStr String 형식의 전송할 메시지 내용
     */
    public Mono<Void> emitMessage(String threadMessageStr) {
        return objectStringConverter.stringToObject(threadMessageStr, ThreadMessageResponseDto.class)
                .doOnNext(threadMessage -> threadWebsocketRepository.getSinks(threadMessage.getThreadId())
                        .tryEmitNext(threadMessage))
                .then();
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
