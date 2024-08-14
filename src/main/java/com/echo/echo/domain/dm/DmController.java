package com.echo.echo.domain.dm;

import com.echo.echo.domain.dm.dto.DmRequest;
import com.echo.echo.domain.dm.dto.DmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dm")
public class DmController {

    private final DmService dmService;

    // DM 생성
    @PostMapping("/create")
    public Mono<ResponseEntity<DmResponse>> createDm(@RequestBody DmRequest dmRequest) {
        return dmService.createDm(dmRequest.getRecipientEmail())
                .map(response -> ResponseEntity.ok().body(response))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    // 전체 DM 조회
    @GetMapping("/all")
    public Flux<DmResponse> getAllDms() {
        return dmService.getAllDms();
    }
    // DM 삭제
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteDmById(@PathVariable String id) {
        return dmService.deleteDmById(id)
                .then(Mono.just(ResponseEntity.noContent().build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
}
