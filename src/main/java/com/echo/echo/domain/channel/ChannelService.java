package com.echo.echo.domain.channel;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.common.exception.codes.ErrorCode;
import com.echo.echo.domain.channel.dto.ChannelRequestDto;
import com.echo.echo.domain.channel.dto.ChannelResponseDto;
import com.echo.echo.domain.channel.entity.Channel;
import com.echo.echo.domain.channel.repository.ChannelRepository;
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
        return spaceRepository.findById(spaceId)
            .switchIfEmpty(Mono.error(new CustomException(ErrorCode.NOT_FOUND)))
            .flatMap(space -> {
                Channel channel = Channel.builder()
                    .spaceId(spaceId)
                    .channelName(requestDto.getChannelName())
                    .channelType(Channel.Type.valueOf(requestDto.getChannelType()).name())
                    .build();
                return channelRepository.save(channel);
            })
            .map(channel -> ChannelResponseDto.builder()
                .id(channel.getId())
                .channelName(channel.getChannelName())
                .channelType(channel.getChannelType())
                .build());
    }

    public Flux<ChannelResponseDto> getChannels(Long spaceId) {
        return channelRepository.findBySpaceId(spaceId)
            .map(channel -> ChannelResponseDto.builder()
                .id(channel.getId())
                .channelName(channel.getChannelName())
                .channelType(channel.getChannelType())
                .build());
    }

    public Mono<ChannelResponseDto> updateChannel(Long channelId, ChannelRequestDto requestDto) {
        return channelRepository.findById(channelId)
            .switchIfEmpty(Mono.error(new CustomException(ErrorCode.NOT_FOUND)))
            .flatMap(channel -> {
                Channel updatedChannel = Channel.builder()
                    .id(channel.getId())
                    .spaceId(channel.getSpaceId())
                    .channelName(requestDto.getChannelName())
                    .channelType(Channel.Type.valueOf(requestDto.getChannelType()).name())
                    .build();
                return channelRepository.save(updatedChannel);
            })
            .map(updatedChannel -> ChannelResponseDto.builder()
                .id(updatedChannel.getId())
                .channelName(updatedChannel.getChannelName())
                .channelType(updatedChannel.getChannelType())
                .build());
    }

    public Mono<Void> deleteChannel(Long channelId) {
        return channelRepository.findById(channelId)
            .switchIfEmpty(Mono.error(new CustomException(ErrorCode.NOT_FOUND)))
            .flatMap(channel -> channelRepository.deleteById(channelId));
    }
}
