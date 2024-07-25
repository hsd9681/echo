package com.echo.echo.domain.user.entity;

import com.echo.echo.domain.text.entity.Text;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {
    @Id
    private Long id;
    private String email;
    private String password;
    private String intro;
    private int status;

    // 상태: 인증 전, 인증 완료, 탈퇴
    public enum Status {
        TEMPORARY, ACTIVATE, DEACTIVATE
    }

    @Builder
    public User(Long id, String email, String password, String intro, Status status) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.intro = intro;
        this.status = status == null? Status.TEMPORARY.ordinal() : status.ordinal();
    }
}
