package com.echo.echo.domain.channel;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.channel.error.ChannelErrorCode;
import com.echo.echo.domain.space.error.SpaceErrorCode;
import com.echo.echo.domain.channel.dto.ChannelRequestDto;
import com.echo.echo.domain.channel.dto.ChannelResponseDto;
import com.echo.echo.domain.channel.entity.Channel;
import com.echo.echo.domain.channel.repository.ChannelRepository;
import com.echo.echo.domain.space.entity.Space;
import com.echo.echo.domain.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * ChannelService는 채널 관련 비즈니스 로직을 처리.
 */

@RequiredArgsConstructor
@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final SpaceRepository spaceRepository;

    public Mono<ChannelResponseDto> createChannel(Long spaceId, ChannelRequestDto requestDto) {
        return findSpaceById(spaceId)
            .flatMap(space -> {
                Channel channel = buildChannel(null, spaceId, requestDto);
                return channelRepository.save(channel);
            })
            .map(this::toChannelResponseDto);
    }

    public Flux<ChannelResponseDto> getChannels(Long spaceId) {
        return channelRepository.findBySpaceId(spaceId)
            .map(this::toChannelResponseDto);
    }

    public Mono<ChannelResponseDto> updateChannel(Long channelId, ChannelRequestDto requestDto) {
        return findChannelById(channelId)
            .flatMap(channel -> {
                Channel updatedChannel = buildChannel(channelId, channel.getSpaceId(), requestDto);
                return channelRepository.save(updatedChannel);
            })
            .map(this::toChannelResponseDto);
    }

    public Mono<Void> deleteChannel(Long channelId) {
        return findChannelById(channelId)
            .flatMap(channelRepository::delete);
    }

    private Mono<Space> findSpaceById(Long spaceId) {
        return spaceRepository.findById(spaceId)
            .switchIfEmpty(Mono.error(new CustomException(SpaceErrorCode.SPACE_NOT_FOUND)));
    }

    private Mono<Channel> findChannelById(Long channelId) {
        return channelRepository.findById(channelId)
            .switchIfEmpty(Mono.error(new CustomException(ChannelErrorCode.CHANNEL_NOT_FOUND)));
    }

    private Channel buildChannel(Long channelId, Long spaceId, ChannelRequestDto requestDto) {
        return Channel.builder()
            .id(channelId)
            .spaceId(spaceId)
            .channelName(requestDto.getChannelName())
            .channelType(Channel.Type.valueOf(requestDto.getChannelType()).name())
            .build();
    }

    private ChannelResponseDto toChannelResponseDto(Channel channel) {
        return ChannelResponseDto.builder()
            .id(channel.getId())
            .channelName(channel.getChannelName())
            .channelType(channel.getChannelType())
            .build();
    }
}
