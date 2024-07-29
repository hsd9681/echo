package com.echo.echo.domain.channel;

import com.echo.echo.domain.channel.error.ChannelSuccessCode;
import com.echo.echo.domain.channel.dto.ChannelRequestDto;
import com.echo.echo.domain.channel.dto.ChannelResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * ChannelController는 채널 관련 API 요청을 처리
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/spaces/{spaceId}/channels")
public class ChannelController {

    private final ChannelFacade channelFacade;

    @PostMapping
    public Mono<ResponseEntity<ChannelResponseDto>> createChannel(@PathVariable Long spaceId, @RequestBody ChannelRequestDto requestDto) {
        return channelFacade.createChannel(spaceId, requestDto)
            .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<ChannelResponseDto> getChannels(@PathVariable Long spaceId) {
        return channelFacade.getChannels(spaceId);
    }

    @PutMapping("/{channelId}")
    public Mono<ResponseEntity<ChannelResponseDto>> updateChannel(@PathVariable Long channelId, @RequestBody ChannelRequestDto requestDto) {
        return channelFacade.updateChannel(channelId, requestDto)
            .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{channelId}")
    public Mono<ResponseEntity<String>> deleteChannel(@PathVariable Long channelId) {
        return channelFacade.deleteChannel(channelId)
            .then(Mono.just(ResponseEntity.ok(ChannelSuccessCode.CHANNEL_DELETE.getMsg())));
    }
}
