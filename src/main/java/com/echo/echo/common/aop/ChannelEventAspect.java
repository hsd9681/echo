package com.echo.echo.common.aop;

import com.echo.echo.domain.channel.ChannelFacade;
import com.echo.echo.domain.channel.dto.ChannelResponseDto;
import com.echo.echo.domain.notification.NotificationService;
import com.echo.echo.domain.notification.SseProcessor;
import com.echo.echo.domain.notification.dto.NotificationDto;
import com.echo.echo.domain.notification.dto.NotificationResponseDto;
import com.echo.echo.domain.notification.entity.Notification;
import com.echo.echo.domain.space.dto.SpaceMemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Aspect
@Slf4j
@Component
public class ChannelEventAspect {

    private final NotificationService notificationService;
    private final ChannelFacade channelFacade;
    private final SseProcessor sseProcessor;

    @Pointcut("execution(* com.echo.echo.domain.channel.ChannelService.createChannel(..))")
    private void createChannelPointcut() {}

    @Pointcut("execution(* com.echo.echo.domain.channel.ChannelService.updateChannel(..))")
    private void updateChannelPointcut() {}

    @Pointcut("execution(* com.echo.echo.domain.channel.ChannelService.deleteChannel(..))")
    private void deleteChannelPointcut() {}

    @Around("createChannelPointcut()")
    public Object aroundChannelCreated(ProceedingJoinPoint joinPoint) throws Throwable {
        Mono<ChannelResponseDto> result = (Mono<ChannelResponseDto>) joinPoint.proceed();

        return result.flatMap(response -> getSpaceMembers(response.getId())
                .flatMap(notification -> sseProcessor.messageSend(notification.getUserId(),
                        toNotificationResponseDto(Notification.EventType.CREATED, response)))
                .then(Mono.just(response))
        );
    }

    private Flux<Long> getSpaceMembersId(Long channelId) {
        return channelFacade.getSpaceMembersIdByChannelId(channelId);
    }

    private Flux<SpaceMemberDto> getSpaceMembers(Long channelId) {
        return channelFacade.getSpaceMembersByChannelId(channelId);
    }

    private NotificationResponseDto toNotificationResponseDto(Notification.EventType eventType, Object data) {
        return NotificationResponseDto.builder()
                .eventType(eventType)
                .notificationType(Notification.NotificationType.CHANNEL)
                .data(data)
                .build();
    }

    private NotificationDto toNotificationDto(Long userId, Long spaceId, Long channelId, Notification.EventType eventType, Object data) {
        return NotificationDto.builder()
                .userId(userId)
                .spaceId(spaceId)
                .channelId(channelId)
                .notificationType(Notification.NotificationType.CHANNEL)
                .eventType(eventType)
                .data(data)
                .build();
    }
}
