package com.echo.echo.domain.space;

import com.echo.echo.common.exception.CommonCode;
import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.space.dto.SpaceRequestDto;
import com.echo.echo.domain.space.dto.SpaceResponseDto;
import com.echo.echo.security.principal.UserPrincipal;
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
    public Mono<ResponseEntity<SpaceResponseDto>> createSpace(@RequestBody SpaceRequestDto requestDto) {
        return spaceFacade.createSpace(requestDto)
            .map(ResponseEntity::ok);
    }

    @PutMapping("/{spaceId}")
    public Mono<ResponseEntity<String>> updateSpace(@PathVariable Long spaceId, @RequestBody SpaceRequestDto requestDto) {
        return spaceFacade.updateSpace(spaceId, requestDto)
            .map(spaceResponse -> ResponseEntity.ok(CommonCode.SUCCESS.getMsg()))
            .onErrorResume(CustomException.class, e -> Mono.just(ResponseEntity.badRequest().body(e.getBaseCode().getCommonReason().getMsg())));
    }


    @GetMapping("/public")
    public Flux<SpaceResponseDto> getAllPublicSpaces() {
        return spaceFacade.getAllPublicSpaces();
    }

    @GetMapping("/{spaceId}")
    public Mono<ResponseEntity<String>> getSpaceById(@PathVariable Long spaceId) {
        return spaceFacade.getSpaceById(spaceId)
            .map(spaceResponse -> ResponseEntity.ok(CommonCode.SUCCESS.getMsg()))
            .onErrorResume(CustomException.class, e -> Mono.just(ResponseEntity.badRequest().body(e.getBaseCode().getCommonReason().getMsg())));
    }


    @DeleteMapping("/{spaceId}")
    public Mono<ResponseEntity<String>> deleteSpace(@PathVariable Long spaceId) {
        return spaceFacade.deleteSpace(spaceId)
            .then(Mono.just(ResponseEntity.ok(CommonCode.DELETE_SUCCESS.getMsg())))
            .onErrorResume(CustomException.class, e -> Mono.just(ResponseEntity.badRequest().body(e.getBaseCode().getCommonReason().getMsg())));
    }


    @PostMapping("/join/{uuid}")
    public Mono<ResponseEntity<String>> joinSpace(@PathVariable String uuid, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return spaceFacade.joinSpace(uuid, userPrincipal.getUser().getId())
            .map(spaceResponse -> ResponseEntity.ok(CommonCode.ENTRY_SUCCESS.getMsg()))
            .onErrorResume(CustomException.class, e -> Mono.just(ResponseEntity.badRequest().body(e.getBaseCode().getCommonReason().getMsg())));
    }



}