package com.echo.echo.domain.user;

import com.echo.echo.domain.user.dto.*;
import com.echo.echo.domain.user.error.UserSuccessCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.echo.echo.domain.auth.dto.VerificationRequest;
import com.echo.echo.domain.mail.Mail;
import com.echo.echo.domain.mail.MailService;
import com.echo.echo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;
    private final MailService mailService;

    // 이메일 유효성 확인 메일 전송
    public Mono<String> createEmailVerificationCode(String email) {
        return userService.createEmailVerificationCode(email)
                .map(code -> {
                    Mono.fromRunnable(() -> {
                        Mail mail = Mail.builder()
                                .to(email)
                                .subject("계정 확인 메일")
                                .body(Mail.createSignupMailBody(String.valueOf(code)))
                                .build();
                        mailService.sendMail(mail)
                                .doOnError(error -> System.err.println("메일 전송 실패: " + error))
                                .subscribe(data -> System.out.println("메일 전송 완료"));
                    }).subscribeOn(Schedulers.boundedElastic()).subscribe();

                    return UserSuccessCode.VERIFICATION_CODE_SENT.getMsg();
                });
    }

    public Mono<String> verifyCode(String uuid, int code) {
        return userService.checkVerificationCode(uuid, code);
    }

    public Mono<UserResponseDto> signup(UserRequestDto req) {
        return userService.signup(req);
    }

    public Mono<FindUserResponseDto> findUserId(String email) {
        return userService.findUserId(email);
    }

    public Mono<String> findUserPassword(String email) {
        return userService.findPassword(email)
                .map(code -> {
                    Mono.fromRunnable(() -> {
                      Mail mail = Mail.builder()
                              .to(email)
                              .subject("비밀번호 변경 인증번호")
                              .body(Mail.createSignupMailBody(String.valueOf(code.getCode())))
                              .build();
                      mailService.sendMail(mail)
                              .subscribe(data -> System.out.println("메일 전송 완료"));
                    }).subscribeOn(Schedulers.boundedElastic()).subscribe();

                    return code.getUuid();
                });
    }

    public Mono<UserResponseDto> findUserById(Long id) {
        return userService.findById(id).map(UserResponseDto::new);
    }

    public Mono<UserResponseDto> updateProfile(Long userId, UpdateProfileRequestDto req) {
        return userService.updateProfile(userId, req).map(UserResponseDto::new);
    }

    public Mono<Void> changePassword(Long userId, ChangePasswordRequestDto req) {
        return userService.changePassword(userId, req)
            .then();
    }

    public Mono<Void> checkVerificationCodeAndChangePassword(String uuid, FindUserDto.Password req) {
        return userService.checkVerificationCodeAndChangePassword(uuid, req)
                .then();
    }

}
