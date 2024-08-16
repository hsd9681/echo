package com.echo.echo.domain.dm;

import com.echo.echo.domain.dm.dto.DmRequest;
import com.echo.echo.domain.dm.dto.DmResponse;
import com.echo.echo.domain.user.entity.User;
import com.echo.echo.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dm")
public class DmController {

    private final DmService dmService;
    private final DmFacade dmFacade;

    // DM 생성
    @PostMapping("/create")
    public Mono<ResponseEntity<DmResponse>> createDm(@RequestBody DmRequest dmRequest,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User serderUser = userPrincipal.getUser();
        return dmFacade.createDm(dmRequest.getRecipientEmail(), serderUser)
                .map(response -> ResponseEntity.ok().body(response))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    // 전체 DM 조회
    @GetMapping
    public Flux<DmResponse> getAllDms(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return dmService.getAllDms(userPrincipal.getUser().getId());
    }

    // DM 삭제
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteDmById(@PathVariable String id) {
        return dmService.deleteDmById(id)
                .then(Mono.just(ResponseEntity.noContent().build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
}
