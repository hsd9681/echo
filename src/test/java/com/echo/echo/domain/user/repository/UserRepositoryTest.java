package com.echo.echo.domain.user.repository;

import com.echo.echo.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void findByIdIn() throws InterruptedException {
        List<Long> ids = List.of(1L, 2L, 3L, 4L);

        userRepository.findByIdIn(ids)
                .doOnNext(user -> System.out.println(user.getEmail()))
                .subscribe();

        Thread.sleep(1000);
    }
}