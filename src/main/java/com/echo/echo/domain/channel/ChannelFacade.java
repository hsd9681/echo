package com.echo.echo.domain.channel;

import com.echo.echo.domain.channel.dto.ChannelRequestDto;
import com.echo.echo.domain.channel.dto.ChannelResponseDto;
import com.echo.echo.domain.notification.NotificationService;
import com.echo.echo.domain.notification.entity.Notification;
import com.echo.echo.domain.space.SpaceService;
import com.echo.echo.domain.space.dto.SpaceMemberDto;
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
    private final NotificationService notificationService;

    public Mono<ChannelResponseDto> createChannel(Long spaceId, ChannelRequestDto requestDto) {
        return spaceService.findSpaceById(spaceId)
            .flatMap(space -> channelService.createChannel(spaceId, requestDto));
    }

    public Flux<ChannelResponseDto> getChannels(Long userId, Long spaceId) {
        return notificationService.getNotificationsTextByUserId(userId)
                .collectMap(Notification::getChannelId)
                .flatMapMany(pushMessage -> channelService.getChannels(spaceId, pushMessage));
    }

    public Mono<ChannelResponseDto> updateChannel(Long channelId, ChannelRequestDto requestDto) {
        return channelService.updateChannel(channelId, requestDto);
    }

    public Mono<Void> deleteChannel(Long channelId) {
        return channelService.deleteChannel(channelId);
    }

    public Flux<SpaceMemberDto> getSpaceMembersByChannelId(Long channelId) {
        return channelService.findChannelById(channelId)
                .flatMapMany(channel -> spaceService.getSpaceMembers(channel.getSpaceId()))
                .map(SpaceMemberDto::new);
    }

    public Mono<ChannelResponseDto> getChannel(Long userId, Long channelId) {
        return notificationService.getNotificationTextByUserId(userId, channelId)
                .flatMap(pushMessage -> channelService.getChannel(channelId, pushMessage));
    }

}
