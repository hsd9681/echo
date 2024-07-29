package com.echo.echo.domain.user;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.user.dto.UserRequestDto;
import com.echo.echo.domain.user.entity.User;
import com.echo.echo.domain.user.error.UserErrorCode;
import com.echo.echo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    protected Mono<User> activateUserStatus(User user) {
        user.activateStatus();
        return userRepository.save(user);
    }

    public Mono<Void> checkVerificationCodeAndActivateUser(int code, String email) {
        return findByEmail(email)
                .filter(user -> user.checkVerificationCode(code))
                .switchIfEmpty(Mono.error(new CustomException(UserErrorCode.INCORRECT_VERIFICATION_NUMBER)))
                .flatMap(this::activateUserStatus).then();
    }

    protected Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new CustomException(UserErrorCode.USER_NOT_FOUND)));
    }

    protected Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    protected Mono<Void> checkDuplicateEmail(String email) {
        return existsByEmail(email)
                .filter(isDuplicated -> !isDuplicated)
                .switchIfEmpty(Mono.error(new CustomException(UserErrorCode.ALREADY_EXIST_EMAIL)))
                .then();
    }
}
