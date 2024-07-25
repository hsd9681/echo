package com.echo.echo.domain.user;

import com.echo.echo.domain.user.dto.UserRequestDto;
import com.echo.echo.domain.user.dto.UserResponseDto;
import com.echo.echo.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserFacade userFacade;

    @PostMapping("/signup")
    public Mono<ResponseEntity<UserResponseDto>> signup(@RequestBody UserRequestDto req) {
        return userFacade.signup(req).map(ResponseEntity::ok);
    }

    @GetMapping("/test")
    public Mono<?> test(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return Mono.just(userPrincipal.getUser().getId());
    }
}
