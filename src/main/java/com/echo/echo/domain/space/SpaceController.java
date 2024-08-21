package com.echo.echo.domain.space;

import com.echo.echo.domain.space.error.SpaceSuccessCode;
import com.echo.echo.domain.space.dto.SpaceRequestDto;
import com.echo.echo.domain.space.dto.SpaceResponseDto;
import com.echo.echo.domain.user.dto.UserResponseDto;
import com.echo.echo.security.principal.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * SpaceController는 스페이스 관련 API 요청을 처리하는 컨트롤러 클래스입니다.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/spaces")
public class SpaceController {

    private final SpaceFacade spaceFacade;

    /**
     * 새로운 스페이스를 생성합니다.
     *
     * @param userPrincipal 현재 인증된 사용자 정보
     * @param requestDto 스페이스 생성 요청 정보
     * @return 생성된 스페이스에 대한 응답
     */
    @PostMapping
    public Mono<ResponseEntity<SpaceResponseDto>> createSpace(
        @AuthenticationPrincipal UserPrincipal userPrincipal,
        @Valid @RequestBody SpaceRequestDto requestDto) {
        Long userId = userPrincipal.getUser().getId();
        return spaceFacade.createSpace(requestDto, userId)
            .map(ResponseEntity::ok);
    }

    /**
     * 기존의 스페이스를 업데이트합니다.
     *
     * @param spaceId 업데이트할 스페이스의 ID
     * @param requestDto 스페이스 업데이트 요청 정보
     * @return 업데이트된 스페이스에 대한 응답
     */
    @PutMapping("/{spaceId}")
    public Mono<ResponseEntity<SpaceResponseDto>> updateSpace(@PathVariable Long spaceId,
        @Valid @RequestBody SpaceRequestDto requestDto) {
        return spaceFacade.updateSpace(spaceId, requestDto)
            .map(ResponseEntity::ok);
    }

    /**
     * 모든 공개된 스페이스를 조회합니다.
     *
     * @return 공개된 스페이스 목록
     */
    @GetMapping("/public")
    public Flux<SpaceResponseDto> getAllPublicSpaces() {
        return spaceFacade.getAllPublicSpaces();
    }

    /**
     * 특정 스페이스를 ID로 조회합니다.
     *
     * @param spaceId 조회할 스페이스의 ID
     * @return 조회된 스페이스에 대한 응답
     */
    @GetMapping("/{spaceId}")
    public Mono<ResponseEntity<SpaceResponseDto>> getSpaceById(@PathVariable Long spaceId) {
        return spaceFacade.getSpaceById(spaceId)
            .map(ResponseEntity::ok);
    }

    /**
     * 스페이스를 삭제합니다.
     *
     * @param spaceId 삭제할 스페이스의 ID
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/{spaceId}")
    public Mono<ResponseEntity<String>> deleteSpace(@PathVariable Long spaceId) {
        return spaceFacade.deleteSpace(spaceId)
            .then(Mono.just(ResponseEntity.ok(SpaceSuccessCode.SPACE_DELETE.getMsg())));
    }

    /**
     * 사용자가 특정 스페이스에 가입합니다.
     *
     * @param userPrincipal 현재 인증된 사용자 정보
     * @param uuid 스페이스의 UUID
     * @return 가입된 스페이스에 대한 응답
     */
    @PostMapping("/join/{uuid}")
    public Mono<ResponseEntity<SpaceResponseDto>> joinSpace(
        @AuthenticationPrincipal UserPrincipal userPrincipal,
        @PathVariable String uuid) {
        return spaceFacade.joinSpace(uuid, userPrincipal.getUser().getId())
            .map(ResponseEntity::ok);
    }

    /**
     * 현재 사용자가 가입한 스페이스 목록을 조회합니다.
     *
     * @param userPrincipal 현재 인증된 사용자 정보
     * @return 사용자가 가입한 스페이스 목록
     */
    @GetMapping("/my")
    public Flux<SpaceResponseDto> getMySpaces(
        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        return spaceFacade.getUserSpaces(userId);
    }

    /**
     * 특정 스페이스의 멤버 목록을 조회합니다.
     *
     * @param spaceId 조회할 스페이스의 ID
     * @return 스페이스의 멤버 목록
     */
    @GetMapping("/{spaceId}/members")
    public Flux<UserResponseDto> getSpaceMembers(@PathVariable Long spaceId) {
        return spaceFacade.getSpaceMembers(spaceId);
    }

}
