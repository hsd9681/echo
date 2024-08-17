package com.echo.echo.domain.auth.dto;

import com.echo.echo.security.jwt.Token;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TokenResponseDto {

    @JsonProperty
    private String accessToken;
    @JsonProperty
    private String refreshToken;

    public TokenResponseDto(Token token) {
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
    }

}
