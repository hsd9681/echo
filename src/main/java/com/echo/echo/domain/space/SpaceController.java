package com.echo.echo.domain.space;

import com.echo.echo.domain.space.dto.SpaceRequestDto;
import com.echo.echo.domain.space.dto.SpaceResponseDto;
import com.echo.echo.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<ResponseEntity<SpaceResponseDto>> updateSpace(@PathVariable Long spaceId, @RequestBody SpaceRequestDto requestDto) {
        return spaceFacade.updateSpace(spaceId, requestDto)
            .map(ResponseEntity::ok);
    }

    @GetMapping("/public")
    public Flux<SpaceResponseDto> getAllPublicSpaces() {
        return spaceFacade.getAllPublicSpaces();
    }

    @GetMapping("/{spaceId}")
    public Mono<ResponseEntity<SpaceResponseDto>> getSpaceById(@PathVariable Long spaceId) {
        return spaceFacade.getSpaceById(spaceId)
            .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{spaceId}")
    public Mono<ResponseEntity<String>> deleteSpace(@PathVariable Long spaceId) {
        return spaceFacade.deleteSpace(spaceId)
            .then(Mono.just(ResponseEntity.ok("삭제 완료입니다")));
    }

    @PostMapping("/join/{uuid}")
    public Mono<ResponseEntity<SpaceResponseDto>> joinSpace(@PathVariable String uuid, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return spaceFacade.joinSpace(uuid, userPrincipal.getUser().getId())
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(ResponseEntity.badRequest()
                .body(SpaceResponseDto.builder().message("입장 실패: 코드가 유효하지 않습니다.").build())));
    }

}
