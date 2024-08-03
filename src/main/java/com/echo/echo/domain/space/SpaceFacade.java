package com.echo.echo.domain.space;

import com.echo.echo.domain.space.dto.SpaceRequestDto;
import com.echo.echo.domain.space.dto.SpaceResponseDto;
import com.echo.echo.domain.user.UserFacade;
import com.echo.echo.domain.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * SpaceFacade는 SpaceService를 통해 스페이스 관련 비즈니스 로직을 처리
 */

@RequiredArgsConstructor
@Component
public class SpaceFacade {

    private final SpaceService spaceService;
    private final UserFacade userFacade;

    public Mono<SpaceResponseDto> createSpace(SpaceRequestDto requestDto, Long userId) {
        return spaceService.createSpace(requestDto, userId);
    }

    public Mono<SpaceResponseDto> updateSpace(Long spaceId, SpaceRequestDto requestDto) {
        return spaceService.updateSpace(spaceId, requestDto);
    }

    public Flux<SpaceResponseDto> getAllPublicSpaces() {
        return spaceService.getAllPublicSpaces();
    }

    public Mono<SpaceResponseDto> getSpaceById(Long spaceId) {
        return spaceService.getSpaceById(spaceId);
    }

    public Mono<Void> deleteSpace(Long spaceId) {
        return spaceService.deleteSpace(spaceId);
    }

    public Mono<SpaceResponseDto> joinSpace(String uuid, Long userId) {
        return spaceService.joinSpace(uuid, userId);
    }

    public Flux<SpaceResponseDto> getUserSpaces(Long userId) {
        return spaceService.getUserSpaces(userId);
    }

    public Flux<UserResponseDto> getSpaceMembers(Long spaceId) {
        return spaceService.getSpaceMembers(spaceId)
            .flatMap(spaceMember -> userFacade.findUserById(spaceMember.getUserId()));
    }


}
