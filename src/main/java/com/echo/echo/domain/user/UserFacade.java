package com.echo.echo.domain.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.echo.echo.domain.auth.dto.VerificationRequest;
import com.echo.echo.domain.mail.Mail;
import com.echo.echo.domain.mail.MailService;
import com.echo.echo.domain.user.dto.ChangePasswordRequestDto;
import com.echo.echo.domain.user.dto.UpdateProfileRequestDto;
import com.echo.echo.domain.user.dto.UserRequestDto;
import com.echo.echo.domain.user.dto.UserResponseDto;
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
    private final PasswordEncoder passwordEncoder;

    public Mono<UserResponseDto> signup(UserRequestDto req) {
        return userService.signup(req)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(user -> {
                    Mail mail = Mail.builder()
                            .to(req.getEmail())
                            .subject("계정 확인 메일")
                            .body(Mail.createSignupMailBody(String.valueOf(user.getVerificationCode())))
                            .build();
                    mailService.sendMail(mail).subscribe(data -> System.out.println("메일 전송 완료"));
                })
                .map(UserResponseDto::new)
            .doOnError(error -> System.err.println("Error: " + error.getMessage()));
    }

    public Mono<Void> verifyCode(int code, VerificationRequest req) {
        return userService.checkVerificationCodeAndActivateUser(code, req.getEmail());
    }

    public Mono<User> findUserByEmail(String email) {
        return userService.findByEmail(email);
    }

    public Mono<User> findUserById(Long id) {
        return userService.findById(id);
    }

    public Mono<User> updateProfile(Long userId, UpdateProfileRequestDto req) {
        return userService.updateProfile(userId, req);
    }

    public Mono<Void> changePassword(Long userId, ChangePasswordRequestDto req) {
        return userService.changePassword(userId, req, passwordEncoder)
            .then();
    }

}
