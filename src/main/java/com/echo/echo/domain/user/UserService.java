package com.echo.echo.domain.user;

import com.echo.echo.domain.user.dto.UserRequestDto;
import com.echo.echo.domain.user.entity.User;
import com.echo.echo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    protected Mono<User> signup(UserRequestDto req) {
        return checkDuplicateEmail(req.getEmail())
                .subscribeOn(Schedulers.boundedElastic())
                .then(Mono.just(User.builder()
                        .email(req.getEmail())
                        .password(passwordEncoder.encode(req.getPassword()))
                        .intro(req.getIntro())
                        .status(User.Status.TEMPORARY)
                        .build())
                )
                .flatMap(userRepository::save)
                .doOnError(error -> System.err.println("Error: " + error.getMessage()));
    }

    protected Mono<User> save(User user) {
        return userRepository.save(user);
    }

    protected Mono<User> findByUserById(Long id) {
        return null;
    }

    protected Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당하는 이메일이 존재하지 않습니다.")));
    }

    protected Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    protected Mono<Void> checkDuplicateEmail(String email) {
        return existsByEmail(email)
                .filter(isDuplicated -> !isDuplicated)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("이미 존재하는 이메일입니다.")))
                .then();
    }
}
