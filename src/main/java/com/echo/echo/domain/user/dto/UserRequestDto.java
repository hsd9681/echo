package com.echo.echo.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRequestDto {
    private String email;
    private String password;
    private String intro;
}
