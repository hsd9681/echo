package com.echo.echo.domain.user;

import com.echo.echo.domain.auth.dto.VerificationRequest;
import com.echo.echo.domain.user.dto.ChangePasswordRequestDto;
import com.echo.echo.domain.user.dto.UpdateProfileRequestDto;
import com.echo.echo.domain.user.dto.UserRequestDto;
import com.echo.echo.domain.user.dto.UserResponseDto;
import com.echo.echo.domain.user.error.UserSuccessCode;
import com.echo.echo.security.principal.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserFacade userFacade;

    @PostMapping("/signup")
    public Mono<ResponseEntity<String>> signup(@Valid @RequestBody UserRequestDto req) {
        return userFacade.signup(req)
            .then(Mono.just(ResponseEntity.ok("회원가입 성공")));
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

    @GetMapping("/profile")
    public Mono<ResponseEntity<UserResponseDto>> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userFacade.findUserById(userPrincipal.getUser().getId())
            .map(UserResponseDto::new)
            .map(ResponseEntity::ok);
    }

    @PutMapping("/profile")
    public Mono<ResponseEntity<UserResponseDto>> updateProfile(@AuthenticationPrincipal UserPrincipal userPrincipal,
        @RequestBody UpdateProfileRequestDto req) {
        return userFacade.updateProfile(userPrincipal.getUser().getId(), req)
            .map(UserResponseDto::new)
            .map(ResponseEntity::ok);
    }

    @PutMapping("/password")
    public Mono<ResponseEntity<String>> changePassword(@AuthenticationPrincipal UserPrincipal userPrincipal,
        @RequestBody ChangePasswordRequestDto req) {
        return userFacade.changePassword(userPrincipal.getUser().getId(), req)
            .then(Mono.just(ResponseEntity.ok(UserSuccessCode.PASSWORD_CHANGE_SUCCESS.getMsg())));
    }
}
