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
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
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

        return messageSend(result, Notification.EventType.CREATED);
    }

    @Around("updateChannelPointcut()")
    public Object aroundChannelUpdated(ProceedingJoinPoint joinPoint) throws Throwable {
        Mono<ChannelResponseDto> result = (Mono<ChannelResponseDto>) joinPoint.proceed();

        return messageSend(result, Notification.EventType.UPDATED);
    }

    @AfterReturning(value = "deleteChannelPointcut()", returning = "result")
    public void aroundChannelDeleted(JoinPoint joinPoint, Mono<Void> result) throws Throwable {
        Long channelId = ((Long) (joinPoint.getArgs()[0]));
        ChannelResponseDto dto = ChannelResponseDto.builder()
                .id(channelId)
                .build();

        messageSend(Mono.just(dto), Notification.EventType.DELETED)
                .subscribe();
    }

    private Mono<ChannelResponseDto> messageSend(Mono<ChannelResponseDto> result, Notification.EventType eventType) {
        return result.flatMap(response -> getSpaceMembers(response.getId())
                .flatMap(notification -> sseProcessor.messageSend(notification.getUserId(),
                        toNotificationResponseDto(eventType, response)))
                .then(Mono.just(response))
        );
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
}
