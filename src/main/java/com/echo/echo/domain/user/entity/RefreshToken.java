package com.echo.echo.domain.user.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshToken {

    private Long id;
    private String email;
    private String nickname;

    @Builder
    public RefreshToken(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }

}
