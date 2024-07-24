package com.echo.echo.domain;

import com.echo.echo.domain.user.UserFacadeService;
import com.echo.echo.domain.user.UserService;
import com.echo.echo.domain.user.dto.UserRequestDto;
import com.echo.echo.domain.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserFacadeService userFacadeService;

    @PostMapping("/signup")
    public Mono<ResponseEntity<UserResponseDto>> signup(@RequestBody UserRequestDto req) {
        return userFacadeService.signup(req)
                .flatMap(res -> Mono.just(ResponseEntity.ok().body(res)));
    }

    @GetMapping("/test")
    public Mono<?> test() {
        return Mono.just("test");
    }
}
