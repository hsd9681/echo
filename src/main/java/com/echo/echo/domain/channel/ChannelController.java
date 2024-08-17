package com.echo.echo.domain.channel;

import com.echo.echo.domain.channel.error.ChannelSuccessCode;
import com.echo.echo.domain.channel.dto.ChannelRequestDto;
import com.echo.echo.domain.channel.dto.ChannelResponseDto;
import com.echo.echo.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import jakarta.validation.Valid;

/**
 * ChannelController는 채널 관련 API 요청을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/spaces/{spaceId}/channels")
public class ChannelController {

    private final ChannelFacade channelFacade;

    /**
     * 새로운 채널을 생성합니다.
     *
     * @param spaceId 생성할 채널이 속한 스페이스 ID
     * @param requestDto 채널 생성 요청 정보
     * @return 생성된 채널에 대한 응답
     */
    @PostMapping
    public Mono<ResponseEntity<ChannelResponseDto>> createChannel(@PathVariable Long spaceId, @Valid @RequestBody ChannelRequestDto requestDto) {
        return channelFacade.createChannel(spaceId, requestDto)
            .map(ResponseEntity::ok);
    }

    /**
     * 특정 스페이스 내의 모든 채널을 조회합니다.
     *
     * @param userPrincipal 현재 인증된 사용자 정보
     * @param spaceId 조회할 스페이스 ID
     * @return 스페이스 내의 채널 목록
     */
    @GetMapping
    public Flux<ChannelResponseDto> getChannels(@AuthenticationPrincipal UserPrincipal userPrincipal,
        @PathVariable Long spaceId) {
        return channelFacade.getChannels(userPrincipal.getId(), spaceId);
    }

    /**
     * 특정 채널을 ID로 조회합니다.
     *
     * @param userPrincipal 현재 인증된 사용자 정보
     * @param channelId 조회할 채널 ID
     * @return 조회된 채널에 대한 응답
     */
    @GetMapping("/{channelId}")
    public Mono<ChannelResponseDto> getChannel(@AuthenticationPrincipal UserPrincipal userPrincipal,
        @PathVariable Long channelId) {
        return channelFacade.getChannel(userPrincipal.getId(), channelId);
    }

    /**
     * 기존의 채널을 업데이트합니다.
     *
     * @param channelId 업데이트할 채널 ID
     * @param requestDto 채널 업데이트 요청 정보
     * @return 업데이트된 채널에 대한 응답
     */
    @PutMapping("/{channelId}")
    public Mono<ResponseEntity<ChannelResponseDto>> updateChannel(@PathVariable Long channelId, @Valid @RequestBody ChannelRequestDto requestDto) {
        return channelFacade.updateChannel(channelId, requestDto)
            .map(ResponseEntity::ok);
    }

    /**
     * 특정 채널을 삭제합니다.
     *
     * @param channelId 삭제할 채널 ID
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/{channelId}")
    public Mono<ResponseEntity<String>> deleteChannel(@PathVariable Long channelId) {
        return channelFacade.deleteChannel(channelId)
            .then(Mono.just(ResponseEntity.ok(ChannelSuccessCode.CHANNEL_DELETE.getMsg())));
    }

}
