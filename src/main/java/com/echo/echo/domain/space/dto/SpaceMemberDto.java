package com.echo.echo.domain.space.dto;

import com.echo.echo.domain.space.entity.SpaceMember;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SpaceMemberDto 클래스는 SpaceMember 엔티티의 데이터를 전달하기 위한 DTO
 */

@NoArgsConstructor
@Getter
public class SpaceMemberDto {

    private Long spaceId;
    private Long userId;

    public SpaceMemberDto(SpaceMember spaceMember) {
        this.spaceId = spaceMember.getSpaceId();
        this.userId = spaceMember.getUserId();
    }

}
