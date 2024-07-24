package com.echo.echo.domain.channel;

import com.echo.echo.domain.channel.entity.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class ChannelFacade {

    private final ChannelService channelService;

    public Mono<Channel> createChannel(Long spaceId, String channelType) {
        return channelService.createChannel(spaceId, channelType);
    }

    public Flux<Channel> getChannel(Long spaceId, String channelType) {
        return channelService.getChannel(spaceId, channelType);
    }

    public Mono<Channel> updateChannel(Long channelId, Long spaceId) {
        return channelService.updateChannel(channelId, spaceId);
    }

    public Mono<Void> deleteChannel(Long channelId) {

        return channelService.deleteChannel(channelId);
    }
}
