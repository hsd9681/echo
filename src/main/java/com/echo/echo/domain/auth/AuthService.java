package com.echo.echo.domain.auth;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.common.exception.codes.CommonErrorCode;
import com.echo.echo.common.redis.RedisService;
import com.echo.echo.domain.auth.dto.TokenRequestDto;
import com.echo.echo.domain.auth.dto.TokenResponseDto;
import com.echo.echo.domain.auth.error.AuthErrorCode;
import com.echo.echo.domain.user.entity.RefreshToken;
import com.echo.echo.domain.user.entity.User;
import com.echo.echo.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

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
                .flatMap(u -> createToken(u.getId(), u.getEmail(), u.getNickname()));
    }
    private Mono<Void> checkPassword(String inputPassword, String password) {
        return Mono.just(passwordEncoder.matches(inputPassword, password))
                .filter(isMatch -> isMatch)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(AuthErrorCode.PASSWORD_NOT_MATCH))))
                .then();
    }

    // 토큰 생성 후, redis에 refresh 토큰 저장
    private Mono<TokenResponseDto> createToken(Long id, String email, String nickname) {
        return jwtProvider.createToken(id, email, nickname)
                .flatMap(token -> saveRefreshToken(id, email, nickname,token.getRefreshToken())
                        .thenReturn(new TokenResponseDto(token)));
    }

    // 동일한 refresh 토큰이 있다면 삭제 후 저장
    private Mono<Void> saveRefreshToken(Long id, String email, String nickname, String refreshToken) {
        return redisService.setValue(refreshToken, RefreshToken.builder()
                                .id(id)
                                .email(email)
                                .nickname(nickname)
                                .build(),
                        Duration.ofMillis(jwtProvider.getRefreshTokenTime()))
                .then();
    }

    private Mono<Void> deleteRefreshToken(String refreshToken) {
        return redisService.deleteValue(refreshToken).then();
    }

    // 토큰 재발급
    public Mono<TokenResponseDto> reissueToken(String inputRefreshToken) {
        return redisService.getCacheValueGeneric(inputRefreshToken, RefreshToken.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN))))
                .flatMap(token -> createToken(token.getId(), token.getEmail(), token.getNickname()))
                .flatMap(newToken -> deleteRefreshToken(inputRefreshToken).then(Mono.just(newToken)));
    }
}
