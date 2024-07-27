package com.echo.echo.domain.user;

import com.echo.echo.domain.auth.AuthFacade;
import com.echo.echo.domain.auth.AuthService;
import com.echo.echo.domain.auth.dto.VerificationRequest;
import com.echo.echo.domain.mail.Mail;
import com.echo.echo.domain.mail.MailService;
import com.echo.echo.domain.user.dto.UserRequestDto;
import com.echo.echo.domain.user.dto.UserResponseDto;
import com.echo.echo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Random;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;
    private final MailService mailService;

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
                .map(UserResponseDto::new);
    }

    public Mono<Void> verifyCode(int code, VerificationRequest req) {
        return userService.checkVerificationCodeAndActivateUser(code, req.getEmail());
    }

    public Mono<User> findUserByEmail(String email) {
        return userService.findByEmail(email);
    }
}
