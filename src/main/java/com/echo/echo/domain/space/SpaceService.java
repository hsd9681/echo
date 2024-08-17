package com.echo.echo.domain.space;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.space.error.SpaceErrorCode;
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
 * SpaceService는 스페이스 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */

@RequiredArgsConstructor
@Service
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceMemberRepository spaceMemberRepository;

    /**
     * 새로운 스페이스를 생성하고, 해당 사용자를 스페이스에 추가합니다.
     *
     * @param requestDto 생성할 스페이스 정보
     * @param userId 생성한 사용자 ID
     * @return 생성된 스페이스의 응답 DTO
     */
    public Mono<SpaceResponseDto> createSpace(SpaceRequestDto requestDto, Long userId) {
        Space space = Space.builder()
            .spaceName(requestDto.getSpaceName())
            .isPublic(requestDto.getIsPublic())
            .thumbnail(requestDto.getThumbnail())
            .build();
        return spaceRepository.save(space)
            .flatMap(savedSpace -> addUserToSpace(userId, savedSpace.getId())
                .thenReturn(new SpaceResponseDto(savedSpace)));
    }

    /**
     * 기존 스페이스의 정보를 업데이트합니다.
     *
     * @param spaceId 업데이트할 스페이스의 ID
     * @param requestDto 업데이트할 스페이스 정보
     * @return 업데이트된 스페이스의 응답 DTO
     */
    public Mono<SpaceResponseDto> updateSpace(Long spaceId, SpaceRequestDto requestDto) {
        return spaceRepository.findById(spaceId)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(SpaceErrorCode.SPACE_NOT_FOUND))))
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

    /**
     * 모든 공개된 스페이스를 조회합니다.
     *
     * @return 공개된 스페이스 목록
     */
    public Flux<SpaceResponseDto> getAllPublicSpaces() {
        return spaceRepository.findAll()
            .filter(space -> "Y".equals(space.getIsPublic()))
            .map(SpaceResponseDto::new);
    }

    /**
     * ID를 통해 특정 스페이스를 조회합니다.
     *
     * @param spaceId 조회할 스페이스의 ID
     * @return 조회된 스페이스의 응답 DTO
     */
    public Mono<SpaceResponseDto> getSpaceById(Long spaceId) {
        return findSpaceById(spaceId)
            .map(SpaceResponseDto::new);
    }

    /**
     * 특정 스페이스를 삭제합니다.
     *
     * @param spaceId 삭제할 스페이스의 ID
     * @return 삭제 완료 후의 작업 처리
     */
    public Mono<Void> deleteSpace(Long spaceId) {
        return findSpaceById(spaceId)
            .flatMap(existingSpace -> spaceMemberRepository.deleteBySpaceId(spaceId)
                .then(spaceRepository.deleteById(spaceId)));
    }

    /**
     * 사용자가 스페이스에 가입합니다.
     *
     * @param uuid 가입할 스페이스의 UUID
     * @param userId 가입하는 사용자의 ID
     * @return 가입된 스페이스의 응답 DTO
     */
    public Mono<SpaceResponseDto> joinSpace(String uuid, Long userId) {
        return spaceRepository.findByUuid(uuid)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(SpaceErrorCode.SPACE_ENTRY_FAILURE))))
            .flatMap(space -> spaceMemberRepository.findByUserIdAndSpaceId(userId, space.getId())
                .flatMap(existingMember -> Mono.error(new CustomException(SpaceErrorCode.SPACE_ALREADY_JOINED)))
                .switchIfEmpty(spaceMemberRepository.save(SpaceMember.builder()
                    .userId(userId)
                    .spaceId(space.getId())
                    .build()))
                .then(Mono.just(space))
                .map(SpaceResponseDto::new)
            );
    }

    /**
     * 특정 사용자가 스페이스 멤버인지 확인합니다.
     *
     * @param spaceId 스페이스 ID
     * @param userId 사용자 ID
     * @return 확인 작업 처리
     */
    public Mono<Void> checkSpaceMember(Long spaceId, Long userId) {
        return spaceMemberRepository.existsBySpaceIdAndUserId(spaceId, userId)
            .filter(exists -> exists)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(SpaceErrorCode.NOT_SPACE_MEMBER))))
            .then();
    }

    /**
     * 특정 스페이스의 멤버 목록을 조회합니다.
     *
     * @param spaceId 스페이스 ID
     * @return 스페이스 멤버 목록
     */
    public Flux<SpaceMember> getSpaceMembers(Long spaceId) {
        return spaceRepository.findById(spaceId)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(SpaceErrorCode.SPACE_NOT_FOUND))))
            .flatMapMany(existingSpace -> spaceMemberRepository.findAllBySpaceId(spaceId));
    }

    /**
     * ID를 통해 스페이스를 조회합니다.
     *
     * @param spaceId 스페이스 ID
     * @return 조회된 스페이스 엔티티
     */
    public Mono<Space> findSpaceById(Long spaceId) {
        return spaceRepository.findById(spaceId)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(SpaceErrorCode.SPACE_NOT_FOUND))));
    }

    /**
     * 사용자가 가입한 스페이스 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자가 가입한 스페이스 목록
     */
    public Flux<SpaceResponseDto> getUserSpaces(Long userId) {
        return spaceMemberRepository.findAllByUserId(userId)
            .flatMap(spaceMember -> spaceRepository.findById(spaceMember.getSpaceId())
                .map(SpaceResponseDto::new))
            .switchIfEmpty(Flux.defer(() -> Flux.error(new CustomException(SpaceErrorCode.NO_SPACES_JOINED))));
    }

    /**
     * 사용자를 스페이스에 추가합니다.
     *
     * @param userId 사용자 ID
     * @param spaceId 스페이스 ID
     * @return 추가된 스페이스 멤버 엔티티
     */
    private Mono<SpaceMember> addUserToSpace(Long userId, Long spaceId) {
        return spaceMemberRepository.save(SpaceMember.builder()
            .userId(userId)
            .spaceId(spaceId)
            .build());
    }

}
