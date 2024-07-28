package com.echo.echo.domain.auth.dto;

import com.echo.echo.security.jwt.Token;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class LoginResponseDto {
    @JsonProperty
    private String accessToken;
    @JsonProperty
    private String refreshToken;

    public LoginResponseDto(Token token) {
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
    }
}
