package com.echo.echo.domain.user.repository;

import com.echo.echo.domain.user.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
}
