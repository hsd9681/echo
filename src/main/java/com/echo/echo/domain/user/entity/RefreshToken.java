package com.echo.echo.domain.user.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshToken {
    private Long id;

    @Builder
    public RefreshToken(Long id) {
        this.id = id;
    }
}
