package com.echo.echo.domain.auth;

import com.echo.echo.domain.auth.dto.LoginRequestDto;
import com.echo.echo.domain.auth.dto.TokenResponseDto;
import com.echo.echo.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class AuthFacade {
    private final AuthService authService;
    private final UserService userService;

    public Mono<TokenResponseDto> login(LoginRequestDto req) {
        return userService.findByEmail(req.getEmail())
                .flatMap(user -> authService.login(req.getPassword(), user));
    }
}
