package com.echo.echo.domain.space.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "space_member")
public class SpaceMember {

    @Column("user_id")
    private Long userId;

    @Column("space_id")
    private Long spaceId;

    public SpaceMember(Long userId, Long spaceId) {
        this.userId = userId;
        this.spaceId = spaceId;
    }
}
