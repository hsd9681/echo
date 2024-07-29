package com.echo.echo.domain.user.dto;

import com.echo.echo.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserResponseDto {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String username;

    @JsonProperty
    private String email;

    @JsonProperty
    private String intro;

    @JsonProperty
    private String status;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.intro = user.getIntro();
        this.status = User.Status.values()[user.getStatus()].name();
    }
}
