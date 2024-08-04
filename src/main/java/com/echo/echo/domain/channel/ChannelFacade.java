package com.echo.echo.domain.channel;

import com.echo.echo.domain.channel.dto.ChannelRequestDto;
import com.echo.echo.domain.channel.dto.ChannelResponseDto;
import com.echo.echo.domain.space.entity.Space;
import com.echo.echo.domain.space.error.SpaceErrorCode;
import com.echo.echo.domain.space.repository.SpaceRepository;
import com.echo.echo.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * ChannelFacade는 채널 관련 비즈니스 로직을 처리하는 서비스 레이어
 */
@RequiredArgsConstructor
@Component
public class ChannelFacade {

    private final ChannelService channelService;
    private final SpaceRepository spaceRepository;

    public Mono<ChannelResponseDto> createChannel(Long spaceId, ChannelRequestDto requestDto) {
        return findSpaceById(spaceId)
            .flatMap(space -> channelService.createChannel(spaceId, requestDto));
    }

    public Flux<ChannelResponseDto> getChannels(Long spaceId) {
        return channelService.getChannels(spaceId);
    }

    public Mono<ChannelResponseDto> updateChannel(Long channelId, ChannelRequestDto requestDto) {
        return channelService.updateChannel(channelId, requestDto);
    }

    public Mono<Void> deleteChannel(Long channelId) {
        return channelService.deleteChannel(channelId);
    }

    private Mono<Space> findSpaceById(Long spaceId) {
        return spaceRepository.findById(spaceId)
            .switchIfEmpty(Mono.error(new CustomException(SpaceErrorCode.SPACE_NOT_FOUND)));
    }
}
