package com.echo.echo.domain.space;

import com.echo.echo.domain.space.dto.SpaceRequestDto;
import com.echo.echo.domain.space.dto.SpaceResponseDto;
import com.echo.echo.domain.space.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spaces")
public class SpaceController {

    private final SpaceService spaceService;

    @PostMapping
    public Mono<ResponseEntity<SpaceResponseDto>> createSpace(@RequestBody SpaceRequestDto requestDto) {
        return spaceService.createSpace(requestDto)
            .map(ResponseEntity::ok);
    }

    @PutMapping("/{spaceId}")
    public Mono<ResponseEntity<SpaceResponseDto>> updateSpace(@PathVariable Long spaceId, @RequestBody SpaceRequestDto requestDto) {
        return spaceService.updateSpace(spaceId, requestDto)
            .map(ResponseEntity::ok);
    }

    @GetMapping("/public")
    public Flux<SpaceResponseDto> getAllPublicSpaces() {
        return spaceService.getAllPublicSpaces();
    }

    @GetMapping("/{spaceId}")
    public Mono<ResponseEntity<SpaceResponseDto>> getSpaceById(@PathVariable Long spaceId) {
        return spaceService.getSpaceById(spaceId)
            .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{spaceId}")
    public Mono<ResponseEntity<String>> deleteSpace(@PathVariable Long spaceId) {
        return spaceService.deleteSpace(spaceId)
            .then(Mono.just(ResponseEntity.ok("삭제 완료입니다")));
    }

}
