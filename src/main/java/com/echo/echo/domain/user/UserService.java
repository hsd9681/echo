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

    protected Mono<User> signup(UserRequestDto userRequestDto) {
        return existsByEmail(userRequestDto.getEmail())
                .flatMap(flag -> {
                    if (flag) {
                        return Mono.error(new IllegalArgumentException(userRequestDto.getEmail() + " already exists"));
                    }
                    return Mono.just(userRequestDto);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then(Mono.just(User.builder()
                                .email(userRequestDto.getEmail())
                                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                                .intro(userRequestDto.getIntro())
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
        return userRepository.findByEmail(email);
    }

    protected Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
