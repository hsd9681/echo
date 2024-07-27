package com.echo.echo.domain.space;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.common.exception.codes.ErrorCode;
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

/**
 * SpaceService는 스페이스 관련 비즈니스 로직을 처리
 */

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
            .map(SpaceResponseDto::new);
    }

    public Mono<SpaceResponseDto> updateSpace(Long spaceId, SpaceRequestDto requestDto) {
        return spaceRepository.findById(spaceId)
            .switchIfEmpty(Mono.error(new CustomException(ErrorCode.NOT_FOUND)))
            .flatMap(existingSpace -> {
                Space updatedSpace = existingSpace.update(
                    requestDto.getSpaceName(),
                    requestDto.getIsPublic(),
                    requestDto.getThumbnail()
                );
                return spaceRepository.save(updatedSpace)
                    .map(SpaceResponseDto::new);
            });
    }

    public Flux<SpaceResponseDto> getAllPublicSpaces() {
        return spaceRepository.findAll()
            .filter(space -> "Y".equals(space.getIsPublic()))
            .map(SpaceResponseDto::new);
    }

    public Mono<SpaceResponseDto> getSpaceById(Long spaceId) {
        return spaceRepository.findById(spaceId)
            .switchIfEmpty(Mono.error(new CustomException(ErrorCode.NOT_FOUND)))
            .map(SpaceResponseDto::new);
    }

    public Mono<Void> deleteSpace(Long spaceId) {
        return spaceRepository.findById(spaceId)
            .switchIfEmpty(Mono.error(new CustomException(ErrorCode.NOT_FOUND)))
            .flatMap(existingSpace -> spaceMemberRepository.deleteBySpaceId(spaceId)
                .then(spaceRepository.deleteById(spaceId)));
    }

    public Mono<SpaceResponseDto> joinSpace(String uuid, Long userId) {
        return spaceRepository.findByUuid(uuid)
            .switchIfEmpty(Mono.error(new CustomException(ErrorCode.ENTRY_FAILURE)))
            .flatMap(space -> spaceMemberRepository.findByUserIdAndSpaceId(userId, space.getId())
                .flatMap(existingMember -> Mono.error(new CustomException(ErrorCode.ALREADY_JOINED)))
                .switchIfEmpty(spaceMemberRepository.save(SpaceMember.builder()
                    .userId(userId)
                    .spaceId(space.getId())
                    .build()).thenReturn(SpaceMember.builder().userId(userId).spaceId(space.getId()).build()))
                .thenReturn(space)
                .map(SpaceResponseDto::new)
            );
    }
}