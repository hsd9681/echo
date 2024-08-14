package com.echo.echo.domain.dm.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "dm")
public class Dm {
    @Id
    private Integer id;  // 자동 증가하는 ID 필드
    private String nickname;  // 사용자 닉네임 추가

    public Dm(Integer id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
