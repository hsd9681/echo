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
    private final UserService userService;

    public Mono<SpaceResponseDto> createSpace(SpaceRequestDto requestDto) {
        Space space = Space.builder()
            .spaceName(requestDto.getSpaceName())
            .isPublic(requestDto.getIsPublic())
            .thumbnail(requestDto.getThumbnail())
            .build();
        return spaceRepository.save(space)
            .map(this::toResponseDto);
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
                    .map(this::toResponseDto);
            });
    }

    public Flux<SpaceResponseDto> getAllPublicSpaces() {
        return spaceRepository.findAll()
            .filter(space -> "Y".equals(space.getIsPublic()))
            .map(this::toResponseDto);
    }

    public Mono<SpaceResponseDto> getSpaceById(Long spaceId) {
        return spaceRepository.findById(spaceId)
            .map(this::toResponseDto);
    }

    public Mono<Void> deleteSpace(Long spaceId) {
        return spaceRepository.deleteById(spaceId);
    }

    private SpaceResponseDto toResponseDto(Space space) {
        return SpaceResponseDto.builder()
            .id(space.getId())
            .spaceName(space.getSpaceName())
            .isPublic(space.getIsPublic())
            .thumbnail(space.getThumbnail())
            .uuid(space.getUuid())
            .build();
    }

    public Mono<SpaceResponseDto> joinSpace(String uuid, String authorization) {
        return userService.extractUserIdFromToken(authorization)
            .flatMap(userId -> spaceRepository.findByUuid(uuid)
                .flatMap(space -> spaceMemberRepository.save(new SpaceMember(userId, space.getId()))
                    .then(Mono.just(space))
                    .map(this::toResponseDto)
                )
            );
    }


}
