package com.echo.echo.domain.user.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user")
public class User {
    @Id
    private Long id;
    private String email;
    private String password;
    private String intro;
    private String nickname;
    private int status;
    private Long kakaoId;

    // 상태: 인증 전, 인증 완료, 탈퇴
    public enum Status {
        ACTIVATE, DEACTIVATE
    }

    @Builder
    public User(Long id, String email, String password, String intro, String nickname, Status status, Long kakaoId) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.intro = intro;
        this.nickname = nickname;
        this.status = status == null ? Status.ACTIVATE.ordinal() : status.ordinal();
        this.kakaoId = kakaoId; // kakaoId 필드 초기화
    }
    public void updateKakaoId(Long kakaoId) {
        this.kakaoId = kakaoId;
    }

    public boolean checkActivate() {
        return this.status == Status.ACTIVATE.ordinal();
    }

    public void updateUsername(String nickname) {
        this.nickname = nickname;
    }

    public void updateIntro(String intro) {
        this.intro = intro;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
