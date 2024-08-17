package com.echo.echo.domain.space.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * SpaceMember 엔티티는 스페이스 멤버 테이블의 데이터를 매핑
 */

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "space_member")
public class SpaceMember {

    @Column("user_id")
    private Long userId;

    @Column("space_id")
    private Long spaceId;

    @Builder
    public SpaceMember(Long userId, Long spaceId) {
        this.userId = userId;
        this.spaceId = spaceId;
    }

}
