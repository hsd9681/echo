package com.echo.echo.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindUserResponseDto {

    private boolean isFind;
    private String msg;

    @Builder
    public FindUserResponseDto(boolean isFind, String msg) {
        this.isFind = isFind;
        this.msg = msg;
    }

}
