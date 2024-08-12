package com.echo.echo.domain.space.dto;

import com.echo.echo.domain.space.entity.SpaceMember;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
