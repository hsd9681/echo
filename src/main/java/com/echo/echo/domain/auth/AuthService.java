package com.echo.echo.domain.auth;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.common.redis.RedisService;
import com.echo.echo.domain.auth.dto.TokenResponseDto;
import com.echo.echo.domain.auth.error.AuthErrorCode;
import com.echo.echo.domain.user.entity.RefreshToken;
import com.echo.echo.domain.user.entity.User;
import com.echo.echo.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    public Mono<TokenResponseDto> login(String inputPassword, User user) {
        return checkPassword(inputPassword, user.getPassword())
                .then(Mono.just(user))
                .filter(User::checkActivate)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(AuthErrorCode.NOT_ACTIVATED_ACCOUNT))))
                .flatMap(validUser -> jwtProvider.createToken(user.getId(), user.getEmail())
                        .flatMap(token -> createToken(user.getId(), token.getRefreshToken())
                                .thenReturn(new TokenResponseDto(token))));
    }

    private Mono<Void> checkPassword(String inputPassword, String password) {
        return Mono.just(passwordEncoder.matches(inputPassword, password))
                .filter(isMatch -> isMatch)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(AuthErrorCode.PASSWORD_NOT_MATCH))))
                .then();
    }

    // 리프레시 토큰 관련
    public Mono<Void> createToken(Long id, String token) {
        return redisService.setValue(token, RefreshToken.builder().id(id).build(),
                Duration.ofMillis(jwtProvider.getRefreshTokenTime())).then();
    }
}
