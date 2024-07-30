package com.echo.echo.domain.user.entity;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.user.error.UserErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user")
public class User {
    @Id
    private Long id;
    private String email;
    private String password;
    private String intro;
    private String username;
    private int status;
    private int verificationCode;

    // 상태: 인증 전, 인증 완료, 탈퇴
    public enum Status {
        TEMPORARY, ACTIVATE, DEACTIVATE
    }

    @Builder
    public User(Long id, String email, String password, String intro, String username, Status status, Integer verificationCode) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.intro = intro;
        this.username = username;
        this.status = status == null ? Status.TEMPORARY.ordinal() : status.ordinal();
        this.verificationCode = verificationCode == null ? new Random(System.currentTimeMillis()).nextInt(900000) + 100000 : verificationCode;
    }

    public boolean checkActivate() {
        return this.status == Status.ACTIVATE.ordinal();
    }

    public void activateStatus() {
        if (this.status == Status.ACTIVATE.ordinal()) {
            throw new CustomException(UserErrorCode.ALREADY_ACCOUNT_ACTIVATED);
        }
        this.status = Status.ACTIVATE.ordinal();
    }

    public boolean checkVerificationCode(int inputCode) {
        return this.getVerificationCode() == inputCode;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateIntro(String intro) {
        this.intro = intro;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
