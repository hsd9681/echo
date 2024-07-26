package com.echo.echo.domain.user;

import com.echo.echo.domain.auth.dto.LoginResponseDto;
import com.echo.echo.domain.auth.dto.VerificationRequest;
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
@RequestMapping("/users")
public class UserController {

    private final UserFacade userFacade;

    @PostMapping("/signup")
    public Mono<ResponseEntity<UserResponseDto>> signup(@RequestBody UserRequestDto req) {
        return userFacade.signup(req).map(ResponseEntity::ok);
    }

    @PostMapping("activate/{code}")
    public Mono<ResponseEntity<String>> activate(@PathVariable("code") int code, @RequestBody VerificationRequest req) {
        return userFacade.verifyCode(code, req)
                .then(Mono.just(ResponseEntity.ok("계정 활성화가 완료되었습니다.")));
    }

    @GetMapping("/test")
    public Mono<?> test(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return Mono.just(userPrincipal.getUser().getId());
    }
}
