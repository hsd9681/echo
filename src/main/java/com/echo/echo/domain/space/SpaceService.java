package com.echo.echo.domain.space;

import com.echo.echo.domain.space.dto.SpaceRequestDto;
import com.echo.echo.domain.space.dto.SpaceResponseDto;
import com.echo.echo.domain.space.entity.Space;
import com.echo.echo.domain.space.entity.SpaceMember;
import com.echo.echo.domain.space.repository.SpaceRepository;
import com.echo.echo.domain.space.repository.SpaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceMemberRepository spaceMemberRepository;

    public Mono<SpaceResponseDto> createSpace(SpaceRequestDto requestDto) {
        Space space = Space.builder()
            .spaceName(requestDto.getSpaceName())
            .isPublic(requestDto.getIsPublic())
            .thumbnail(requestDto.getThumbnail())
            .build();
        return spaceRepository.save(space)
            .map(spaceEntity -> toResponseDto(spaceEntity, null));
    }

    public Mono<SpaceResponseDto> updateSpace(Long spaceId, SpaceRequestDto requestDto) {
        return spaceRepository.findById(spaceId)
            .flatMap(existingSpace -> {
                Space updatedSpace = existingSpace.update(
                    requestDto.getSpaceName(),
                    requestDto.getIsPublic(),
                    requestDto.getThumbnail()
                );
                return spaceRepository.save(updatedSpace)
                    .map(spaceEntity -> toResponseDto(spaceEntity, null));
            });
    }

    public Flux<SpaceResponseDto> getAllPublicSpaces() {
        return spaceRepository.findAll()
            .filter(space -> "Y".equals(space.getIsPublic()))
            .map(spaceEntity -> toResponseDto(spaceEntity, null));
    }

    public Mono<SpaceResponseDto> getSpaceById(Long spaceId) {
        return spaceRepository.findById(spaceId)
            .map(spaceEntity -> toResponseDto(spaceEntity, null));
    }

    public Mono<Void> deleteSpace(Long spaceId) {
        return spaceRepository.deleteById(spaceId);
    }

    private SpaceResponseDto toResponseDto(Space space, String message) {
        return SpaceResponseDto.builder()
            .id(space.getId())
            .spaceName(space.getSpaceName())
            .isPublic(space.getIsPublic())
            .thumbnail(space.getThumbnail())
            .uuid(space.getUuid())
            .message(message)
            .build();
    }

    public Mono<SpaceResponseDto> joinSpace(String uuid, Long userId) {
        return spaceRepository.findByUuid(uuid)
            .flatMap(space -> spaceMemberRepository.findByUserIdAndSpaceId(userId, space.getId())
                .switchIfEmpty(spaceMemberRepository.save(new SpaceMember(userId, space.getId())).thenReturn(new SpaceMember(userId, space.getId())))
                .thenReturn(space)
                .map(spaceEntity -> toResponseDto(spaceEntity, "입장 성공입니다"))
            );
    }
}
