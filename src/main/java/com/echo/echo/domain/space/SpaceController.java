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
 * SpaceController는 스페이스 관련 API 요청을 처리
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/spaces")
public class SpaceController {

    private final SpaceFacade spaceFacade;

    @PostMapping
    public Mono<ResponseEntity<SpaceResponseDto>> createSpace(
        @AuthenticationPrincipal UserPrincipal userPrincipal,
        @Valid @RequestBody SpaceRequestDto requestDto) {
        Long userId = userPrincipal.getUser().getId();
        return spaceFacade.createSpace(requestDto, userId)
            .map(ResponseEntity::ok);
    }

    @PutMapping("/{spaceId}")
    public Mono<ResponseEntity<SpaceResponseDto>> updateSpace(@PathVariable Long spaceId,
        @Valid @RequestBody SpaceRequestDto requestDto) {
        return spaceFacade.updateSpace(spaceId, requestDto)
            .map(ResponseEntity::ok);
    }

    @GetMapping("/public")
    public Flux<SpaceResponseDto> getAllPublicSpaces() {
        return spaceFacade.getAllPublicSpaces();
    }

    @GetMapping("/{spaceId}")
    public Mono<ResponseEntity<Object>> getSpaceById(@PathVariable Long spaceId) {
        return spaceFacade.getSpaceById(spaceId)
            .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{spaceId}")
    public Mono<ResponseEntity<String>> deleteSpace(@PathVariable Long spaceId) {
        return spaceFacade.deleteSpace(spaceId)
            .then(Mono.just(ResponseEntity.ok(SpaceSuccessCode.SPACE_DELETE.getMsg())));
    }

    @PostMapping("/join/{uuid}")
    public Mono<ResponseEntity<Object>> joinSpace(
        @AuthenticationPrincipal UserPrincipal userPrincipal,
        @PathVariable String uuid) {
        return spaceFacade.joinSpace(uuid, userPrincipal.getUser().getId())
            .map(ResponseEntity::ok);
    }

    @GetMapping("/my")
    public Flux<SpaceResponseDto> getMySpaces(
        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        return spaceFacade.getUserSpaces(userId);
    }

    @GetMapping("/{spaceId}/members")
    public Flux<UserResponseDto> getSpaceMembers(@PathVariable Long spaceId) {
        return spaceFacade.getSpaceMembers(spaceId);
    }

}
