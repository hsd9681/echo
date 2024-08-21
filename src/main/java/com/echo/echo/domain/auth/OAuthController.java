package com.echo.echo.domain.auth;

import com.echo.echo.domain.auth.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthFacade oAuthFacade;

    @GetMapping("/user/kakao/callback")
    public Mono<TokenResponseDto> kakaoLogin(@RequestParam String code) {
        return oAuthFacade.handleKakaoLogin(code);
    }

}
