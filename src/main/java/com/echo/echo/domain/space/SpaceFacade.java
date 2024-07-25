package com.echo.echo.domain.space;

import com.echo.echo.domain.space.dto.SpaceRequestDto;
import com.echo.echo.domain.space.dto.SpaceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class SpaceFacade {

    private final SpaceService spaceService;

    public Mono<SpaceResponseDto> createSpace(SpaceRequestDto requestDto) {
        return spaceService.createSpace(requestDto);
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
}
