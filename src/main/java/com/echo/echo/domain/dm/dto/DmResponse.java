package com.echo.echo.domain.dm.dto;

import com.echo.echo.domain.dm.entity.Dm;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DmResponse {

    private int id;
    private String nickname;

    public DmResponse(Dm dm) {
        this.id = dm.getId();
        this.nickname = dm.getNickname();
    }
}
