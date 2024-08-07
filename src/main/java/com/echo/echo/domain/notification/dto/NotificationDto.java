package com.echo.echo.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NotificationDto {
    @JsonProperty
    private String message;

    public NotificationDto(String message) {
        this.message = message;
    }
}
