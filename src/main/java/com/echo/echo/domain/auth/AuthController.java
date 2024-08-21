package com.echo.echo.domain.auth;

import com.echo.echo.domain.auth.dto.LoginRequestDto;
import com.echo.echo.domain.auth.dto.TokenRequestDto;
import com.echo.echo.domain.auth.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("login")
    public Mono<ResponseEntity<TokenResponseDto>> login(@RequestBody LoginRequestDto req) {
        return authFacade.login(req).map(ResponseEntity::ok);
    }

    @PostMapping("reissue")
    public Mono<ResponseEntity<TokenResponseDto>> reissueToken(@RequestBody TokenRequestDto req) {
        return authFacade.reissueToken(req.getToken()).map(ResponseEntity::ok);
    }

}
