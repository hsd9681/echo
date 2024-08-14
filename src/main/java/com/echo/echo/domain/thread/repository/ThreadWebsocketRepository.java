package com.echo.echo.domain.thread.repository;

import com.echo.echo.domain.thread.dto.ThreadMessageResponseDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread에 대한 Sink를 관리합니다.
 */
@Component
public class ThreadWebsocketRepository {
    private final Map<Long, Sinks.Many<ThreadMessageResponseDto>> sinkByThread = new ConcurrentHashMap<>();

    /**
     * 해당하는 스레드의 sink를 가져온다. 없으면 생성
     * @param threadId 해당 스레드 고유 번호
     */
    public Sinks.Many<ThreadMessageResponseDto> getSinks(Long threadId) {
        return sinkByThread.computeIfAbsent(threadId,
                unused -> Sinks.many().multicast().directAllOrNothing());
    }

    /**
     * 스레드가 삭제되면 sink에서 해당하는 sink를 삭제한다.
     * @param threadId 삭제할 스레드 고유 번호
     */
    public void deleteSink(Long threadId) {
        sinkByThread.remove(threadId);
    }
}
