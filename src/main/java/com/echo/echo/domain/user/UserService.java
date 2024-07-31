package com.echo.echo.domain.user;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.user.dto.ChangePasswordRequestDto;
import com.echo.echo.domain.user.dto.UpdateProfileRequestDto;
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
                        .username(req.getUsername())
                        .status(User.Status.TEMPORARY)
                        .build())
                )
                .flatMap(userRepository::save);
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
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(UserErrorCode.INCORRECT_VERIFICATION_NUMBER))))
                .flatMap(this::activateUserStatus).then();
    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(UserErrorCode.USER_NOT_FOUND))));
    }

    protected Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    protected Mono<Void> checkDuplicateEmail(String email) {
        return existsByEmail(email)
                .filter(isDuplicated -> !isDuplicated)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(UserErrorCode.ALREADY_EXIST_EMAIL))))
                .then();
    }

    public Mono<User> findById(Long id) {
        return userRepository.findById(id)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(UserErrorCode.USER_NOT_FOUND))));
    }

    public Mono<User> updateProfile(Long userId, UpdateProfileRequestDto req) {
        return findById(userId)
            .flatMap(user -> {
                user.updateUsername(req.getUsername());
                user.updateIntro(req.getIntro());
                return save(user);
            });
    }

    public Mono<Void> changePassword(Long userId, ChangePasswordRequestDto req, PasswordEncoder passwordEncoder) {
        return findById(userId)
            .flatMap(user -> {
                if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                    return Mono.error(new CustomException(UserErrorCode.PASSWORD_NOT_MATCH));
                }
                user.updatePassword(passwordEncoder.encode(req.getNewPassword()));
                return save(user);
            })
            .then();
    }

}
