package com.echo.echo.domain.channel;

import com.echo.echo.domain.channel.dto.ChannelRequestDto;
import com.echo.echo.domain.channel.dto.ChannelResponseDto;
import com.echo.echo.domain.space.SpaceService;
import com.echo.echo.domain.space.dto.SpaceMemberDto;
import com.echo.echo.domain.space.dto.SpaceResponseDto;
import com.echo.echo.domain.space.entity.SpaceMember;
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
    private final SpaceService spaceService;

    public Mono<ChannelResponseDto> createChannel(Long spaceId, ChannelRequestDto requestDto) {
        return spaceService.findSpaceById(spaceId)
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

    // 여기에 채널 아이디로 스페이스 아이디를 가져오고 해당하는 스페이스 멤버를 가져오는 메서드를 작성합니다.
    public Flux<Long> getSpaceMembersIdByChannelId(Long channelId) {
        return channelService.findChannelById(channelId)
                .flatMapMany(channel -> spaceService.getSpaceMembers(channel.getSpaceId()))
                .map(SpaceMember::getUserId);
    }

    // 여기에 채널 아이디로 스페이스 아이디를 가져오고 해당하는 스페이스 멤버를 가져오는 메서드를 작성합니다.
    public Flux<SpaceMemberDto> getSpaceMembersByChannelId(Long channelId) {
        return channelService.findChannelById(channelId)
                .flatMapMany(channel -> spaceService.getSpaceMembers(channel.getSpaceId()))
                .map(SpaceMemberDto::new);
    }

}
