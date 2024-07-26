package com.echo.echo.domain.auth;

import com.echo.echo.domain.auth.dto.LoginRequestDto;
import com.echo.echo.domain.auth.dto.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("login")
    public Mono<ResponseEntity<LoginResponseDto>> login(@RequestBody LoginRequestDto req) {
        return authFacade.login(req).map(ResponseEntity::ok);
    }
}
