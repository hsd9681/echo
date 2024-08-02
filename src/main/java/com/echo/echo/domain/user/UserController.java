package com.echo.echo.domain.user;

import com.echo.echo.domain.auth.dto.VerificationRequest;
import com.echo.echo.domain.user.dto.*;
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

    /**
     * 회원가입 시 이메일 인증번호 전송
     */
    @PostMapping("/verify/email")
    public Mono<ResponseEntity<String>> createEmailVerificationCode(@Valid @RequestBody VerificationRequest req) {
        return userFacade.createEmailVerificationCode(req.getEmail()).map(ResponseEntity::ok);
    }


    /**
     * 회원가입 시 인증코드 입력
     */
    @PostMapping("verify/{code}")
    public Mono<ResponseEntity<String>> verify(@PathVariable("code") int code,
                                               @Valid @RequestBody VerificationRequest req) {
        return userFacade.verifyCode(req.getEmail(), code).map(ResponseEntity::ok);
    }

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public Mono<ResponseEntity<UserResponseDto>> signup(@Valid @RequestBody UserRequestDto req) {
        return userFacade.signup(req).map(ResponseEntity::ok);
    }

    /**
     * 비밀번호 찾기 시 인증코드 입력
     */
    @PostMapping("verify/{uuid}/{code}")
    public Mono<ResponseEntity<String>> verify(@PathVariable("uuid") String uuid,
                                               @PathVariable("code") int code) {
        return userFacade.verifyCode(uuid, code).map(ResponseEntity::ok);
    }

    /**
     * 유저 정보 조회
     */
    @GetMapping("/profile")
    public Mono<ResponseEntity<UserResponseDto>> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userFacade.findUserById(userPrincipal.getUser().getId())
                .map(ResponseEntity::ok);
    }

    /**
     * 유저 정보 수정
     */
    @PutMapping("/profile")
    public Mono<ResponseEntity<UserResponseDto>> updateProfile(@AuthenticationPrincipal UserPrincipal userPrincipal,
        @RequestBody UpdateProfileRequestDto req) {
        return userFacade.updateProfile(userPrincipal.getUser().getId(), req)
                .map(ResponseEntity::ok);
    }

    /**
     * 유저 정보 -> 패스워드 변경
     */
    @PutMapping("/password")
    public Mono<ResponseEntity<String>> changePassword(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                       @RequestBody ChangePasswordRequestDto req) {
        return userFacade.changePassword(userPrincipal.getUser().getId(), req)
            .then(Mono.just(ResponseEntity.ok(UserSuccessCode.PASSWORD_CHANGE_SUCCESS.getMsg())));
    }

    /**
     * 유저 아이디 찾기
     */
    @PostMapping("find/id")
    public Mono<ResponseEntity<FindUserResponseDto>> findId(@Valid @RequestBody FindUserDto req) {
        return userFacade.findUserId(req.getEmail()).map(ResponseEntity::ok);
    }

    /**
     * 유저 비밀번호 찾기
     */
    @PostMapping("find/password")
    public Mono<ResponseEntity<?>> findPassword(@RequestBody FindUserDto req) {
        return userFacade.findUserPassword(req.getEmail()).map(ResponseEntity::ok);
    }

    /**
     * 유저 비밀번호 변경(비인증)
     */
    @PutMapping("change/password/{uuid}")
    public Mono<ResponseEntity<String>> changePassword(@PathVariable("uuid") String uuid,
                                                       @Valid @RequestBody FindUserDto.Password req) {
        return userFacade.checkVerificationCodeAndChangePassword(uuid, req)
                .then(Mono.just(ResponseEntity.ok(UserSuccessCode.PASSWORD_CHANGE_SUCCESS.getMsg())));
    }
}
