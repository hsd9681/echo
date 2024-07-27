package com.echo.echo.domain.auth;

import com.echo.echo.domain.auth.dto.LoginRequestDto;
import com.echo.echo.domain.auth.dto.LoginResponseDto;
import com.echo.echo.domain.auth.dto.VerificationRequest;
import com.echo.echo.domain.user.UserFacade;
import com.echo.echo.domain.user.entity.User;
import com.echo.echo.security.jwt.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class AuthFacade {
    private final AuthService authService;
    private final UserFacade userFacade;

    public Mono<LoginResponseDto> login(LoginRequestDto req) {
        return userFacade.findUserByEmail(req.getEmail())
                .flatMap(user -> authService.login(req.getPassword(), user));
    }
}
