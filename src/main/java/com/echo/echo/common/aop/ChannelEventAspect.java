package com.echo.echo.common.aop;

import com.echo.echo.domain.channel.ChannelFacade;
import com.echo.echo.domain.channel.ChannelService;
import com.echo.echo.domain.channel.dto.ChannelResponseDto;
import com.echo.echo.domain.notification.SseProcessor;
import com.echo.echo.domain.notification.dto.NotificationDto;
import com.echo.echo.domain.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Aspect
@Slf4j
@Component
public class ChannelEventAspect {

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
                    .flatMap(id -> sseProcessor.messageSend(id, toNotificationDto(Notification.EventType.CREATED, response)))
                    .then(Mono.just(response))
        );
    }

    private Flux<Long> getSpaceMembers(Long channelId) {
        return channelFacade.getSpaceMembersByChannelId(channelId);
    }

    private NotificationDto toNotificationDto(Notification.EventType eventType, Object data) {
        return NotificationDto.builder()
                .eventType(eventType)
                .notificationType(Notification.NotificationType.CHANNEL)
                .data(data)
                .build();
    }
}
