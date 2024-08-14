package com.echo.echo.domain.thread.repository;

import com.echo.echo.domain.thread.entity.Thread;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ThreadRepository extends ReactiveCrudRepository<Thread, Long> {
    @Query("select t.*, u.email, u.nickname from thread t " +
            "left join user u on t.creator_id = u.id " +
            "where t.id = :id")
    Mono<Thread> findByIdWithUser(Long id);


    @Query("select t.*, u.email, u.nickname from thread t " +
            "left join user u on t.creator_id = u.id " +
            "where t.channel_id = :channelId")
    Flux<Thread> findAllByChannelIdWithUser(Long channelId);

    Mono<Boolean> existsByTextId(String textId);
}
