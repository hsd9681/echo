package com.echo.echo.domain.space;

import com.echo.echo.common.exception.codes.CommonCode;
import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.space.dto.SpaceRequestDto;
import com.echo.echo.domain.space.dto.SpaceResponseDto;
import com.echo.echo.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public Mono<ResponseEntity<SpaceResponseDto>> createSpace(@RequestBody SpaceRequestDto requestDto) {
        return spaceFacade.createSpace(requestDto)
            .map(ResponseEntity::ok);
    }

    @PutMapping("/{spaceId}")
    public Mono<ResponseEntity<SpaceResponseDto>> updateSpace(@PathVariable Long spaceId,
                                                              @RequestBody SpaceRequestDto requestDto) {
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
            .then(Mono.just(ResponseEntity.ok(CommonCode.DELETE_SUCCESS.getMsg())));
    }

    @PostMapping("/join/{uuid}")
    public Mono<ResponseEntity<Object>> joinSpace(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @PathVariable String uuid) {
        return spaceFacade.joinSpace(uuid, userPrincipal.getUser().getId())
            .map(ResponseEntity::ok);
    }

}