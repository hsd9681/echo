package com.echo.echo.domain.user.dto;

import lombok.Getter;

@Getter
public class FindUserDto {
    private String email;

    @Getter
    public static class Password {
        private String email;
        private String newPassword;
    }
}
