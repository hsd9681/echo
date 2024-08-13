package com.echo.echo.domain.thread.repository;

import com.echo.echo.domain.thread.entity.Thread;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ThreadRepositoryTest {

    @Autowired
    private ThreadRepository threadRepository;

    @DisplayName("ThreadRepositoryTest: convert 확인")
    @Test
    void findByIdWithUser() throws InterruptedException {
        Mono<Thread> thread = threadRepository.findByIdWithUser(1L);

        thread.map(t -> t)
                .doOnNext(t -> System.out.println("thread: " + t.getUser().getEmail()))
                .subscribe();

        java.lang.Thread.sleep(1000);
    }
}