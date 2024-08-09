// OAuthFacade.java
package com.echo.echo.domain.auth;

import com.echo.echo.domain.auth.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class OAuthFacade {

    private final KakaoService kakaoService;

    public Mono<TokenResponseDto> handleKakaoLogin(String code) {
        return kakaoService.kakaoLogin(code);
    }
}
