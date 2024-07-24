package com.echo.echo.domain.channel;

import com.echo.echo.domain.channel.entity.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChannelController {

    private final ChannelFacade channelFacade;

    @PostMapping("/spaces/{spaceId}/channels")
    public Mono<ResponseEntity<Channel>> createChannel(@PathVariable Long spaceId, @RequestParam String channelType) {
        return channelFacade.createChannel(spaceId,channelType).map(ResponseEntity::ok);
    }

    @GetMapping("/spaces/{spaceId}/channels")
    public Flux<ResponseEntity<Channel>> getChannel(@PathVariable Long spaceId, @RequestParam String channelType) {
        return channelFacade.getChannel(spaceId, channelType).map(ResponseEntity::ok);
    }

    @PutMapping("/spaces/{spaceId}/channels/{channelId}")
    public Mono<ResponseEntity<Channel>> updateChannel(@PathVariable Long spaceId, @PathVariable Long channelId) {
        return channelFacade.updateChannel(spaceId, channelId).map(ResponseEntity::ok);
    }

    @DeleteMapping("/spaces/{spaceId}/channels/{channelId}")
    public Mono<ResponseEntity<Void>> deleteChannel(@RequestBody Long channelId) {
        return channelFacade.deleteChannel(channelId).then(Mono.just(ResponseEntity.noContent().build()));
    }

}


