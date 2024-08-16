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
 * ChannelController는 채널 관련 API 요청을 처리
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/spaces/{spaceId}/channels")
public class ChannelController {

    private final ChannelFacade channelFacade;

    @PostMapping
    public Mono<ResponseEntity<ChannelResponseDto>> createChannel(@PathVariable Long spaceId, @Valid @RequestBody ChannelRequestDto requestDto) {
        return channelFacade.createChannel(spaceId, requestDto)
            .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<ChannelResponseDto> getChannels(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                @PathVariable Long spaceId) {
        return channelFacade.getChannels(userPrincipal.getId(), spaceId);
    }

    @PutMapping("/{channelId}")
    public Mono<ResponseEntity<ChannelResponseDto>> updateChannel(@PathVariable Long channelId, @Valid @RequestBody ChannelRequestDto requestDto) {
        return channelFacade.updateChannel(channelId, requestDto)
            .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{channelId}")
    public Mono<ResponseEntity<String>> deleteChannel(@PathVariable Long channelId) {
        return channelFacade.deleteChannel(channelId)
            .then(Mono.just(ResponseEntity.ok(ChannelSuccessCode.CHANNEL_DELETE.getMsg())));
    }
}
