package com.echo.echo.domain.auth;

import com.echo.echo.common.redis.RedisService;
import com.echo.echo.domain.auth.dto.KakaoUserInfoDto;
import com.echo.echo.domain.auth.dto.TokenResponseDto;
import com.echo.echo.domain.user.entity.RefreshToken;
import com.echo.echo.domain.user.entity.User;
import com.echo.echo.domain.user.repository.UserRepository;
import com.echo.echo.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    public Mono<TokenResponseDto> kakaoLogin(String code) {
        return getAccessToken(code)
                .flatMap(this::getKakaoUserInfo)
                .flatMap(this::registerKakaoUserIfNeeded)
                .flatMap(user -> generateTokens(user));
    }

    private Mono<String> getAccessToken(String code) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build())
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        return jsonNode.get("access_token").asText();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse access token", e);
                    }
                });
    }

    private Mono<KakaoUserInfoDto> getKakaoUserInfo(String accessToken) {
        return webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(responseBody -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(responseBody);
                        Long id = jsonNode.get("id").asLong();
                        String nickname = jsonNode.path("properties").path("nickname").asText();
                        String email = jsonNode.path("kakao_account").path("email").asText();

                        // 사용자 정보를 로깅
                        log.info("카카오 사용자 정보: ID: " + id + ", Nickname: " + nickname + ", Email: " + email);

                        // 사용자 정보 DTO를 Mono로 감싸서 반환
                        return Mono.just(new KakaoUserInfoDto(id, nickname, email));
                    } catch (Exception e) {
                        // 예외 처리 및 로그
                        log.error("Failed to parse Kakao user info: " + e.getMessage(), e);
                        return Mono.error(new RuntimeException("Failed to parse Kakao user info", e));
                    }
                });
    }
    private Mono<User> registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        Long kakaoId = kakaoUserInfo.getId();
        String kakaoEmail = kakaoUserInfo.getEmail();

        return userRepository.findByKakaoId(kakaoId)
                .switchIfEmpty(
                        userRepository.findByEmail(kakaoEmail)
                                .flatMap(existingUser -> {
                                    existingUser.updateKakaoId(kakaoId);
                                    return userRepository.save(existingUser);
                                })
                                .switchIfEmpty(
                                        Mono.defer(() -> {
                                            String password = UUID.randomUUID().toString();
                                            String encodedPassword = passwordEncoder.encode(password);

                                            User newUser = User.builder()
                                                    .nickname(kakaoUserInfo.getNickname())
                                                    .password(encodedPassword)
                                                    .email(kakaoEmail)
                                                    .status(User.Status.ACTIVATE)
                                                    .kakaoId(kakaoId)
                                                    .build();

                                            return userRepository.save(newUser);
                                        })
                                )
                );
    }

    //토큰생성 부분
    private Mono<TokenResponseDto> generateTokens(User user) {
        return jwtProvider.createToken(user.getId(), user.getEmail(), user.getNickname())
                .flatMap(token -> saveRefreshToken(user.getId(), user.getEmail(), user.getNickname(), token.getRefreshToken())
                        .thenReturn(new TokenResponseDto(token)));
    }

    private Mono<Void> saveRefreshToken(Long id, String email, String nickname, String refreshToken) {
        return redisService.setValue(refreshToken, RefreshToken.builder()
                                .id(id)
                                .email(email)
                                .nickname(nickname)
                                .build(),
                        Duration.ofMillis(jwtProvider.getRefreshTokenTime()))
                .then();
    }


}
