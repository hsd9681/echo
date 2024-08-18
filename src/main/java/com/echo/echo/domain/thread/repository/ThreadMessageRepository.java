package com.echo.echo.domain.thread.repository;

import com.echo.echo.domain.thread.entity.ThreadMessage;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ThreadMessageRepository extends ReactiveCrudRepository<ThreadMessage, Long> {

    @Query("select tm.*, u.email, u.nickname, " +
            "t.text_id, t.status  from thread_message tm " +
            "left join user u on tm.author_id = u.id " +
            "left join thread t on tm.thread_id = t.id " +
            "where tm.thread_id = :threadId")
    Flux<ThreadMessage> findAllByThreadId(Long threadId);

}
