package com.echo.echo.common.aop;

import com.echo.echo.common.redis.RedisConst;
import com.echo.echo.common.redis.RedisPublisher;
import com.echo.echo.common.util.ObjectStringConverter;
import com.echo.echo.domain.channel.ChannelFacade;
import com.echo.echo.domain.channel.dto.ChannelResponseDto;
import com.echo.echo.domain.notification.NotificationFacade;
import com.echo.echo.domain.notification.dto.NotificationDto;
import com.echo.echo.domain.notification.dto.NotificationResponseDto;
import com.echo.echo.domain.notification.entity.Notification;
import com.echo.echo.domain.space.dto.SpaceMemberDto;
import com.echo.echo.domain.text.dto.TextResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RequiredArgsConstructor
@Aspect
@Slf4j
@Component
public class SseAspect {

    private final NotificationFacade notificationFacade;
    private final ChannelFacade channelFacade;
    private final RedisPublisher redisPublisher;
    private final ObjectStringConverter objectStringConverter;

    @Pointcut("execution(* com.echo.echo.domain.channel.ChannelService.createChannel(..))")
    private void createChannelPointcut() {}

    @Pointcut("execution(* com.echo.echo.domain.channel.ChannelService.updateChannel(..))")
    private void updateChannelPointcut() {}

    @Pointcut("execution(* com.echo.echo.domain.channel.ChannelService.deleteChannel(..))")
    private void deleteChannelPointcut() {}

    @Pointcut("execution(* com.echo.echo.domain.text.controller.TextWebSocketHandler.sendText(..))")
    private void sendMessagePointcut() {}

    @Pointcut("execution(* com.echo.echo.domain.text.TextService.startSession(..))")
    private void startSessionPointcut() {}

    @Pointcut("execution(* com.echo.echo.domain.channel.ChannelService.checkAndIncrementMemberCount(..)) || execution(* com.echo.echo.domain.channel.ChannelService.decrementMemberCount(..))")
    private void memberCountChangePointcut() {}

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

    @Around("memberCountChangePointcut()")
    public Object memberCountChangePointcut(ProceedingJoinPoint joinPoint) throws Throwable {
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

    // ** websocket send message ** //

    @AfterReturning(value = "sendMessagePointcut()", returning = "result")
    public void afterReturningSendMessage(JoinPoint joinPoint, Mono<?> result) {
        String body = (String)(joinPoint.getArgs()[0]);

        // sendMessage가 발생 시, 모든 space의 멤버에 Notification 데이터를 저장.
        objectStringConverter.stringToObject(body, TextResponse.class)
                .filter(response -> response.getChannelId() != null)
                .flatMapMany(response -> getSpaceMembers(response.getChannelId())
                       // 메시지 보낸 사람은 제외하고 전송 및 저장
                       .filter(spaceMemberDto -> !Objects.equals(spaceMemberDto.getUserId(), response.getUserId()))
                       .flatMap(spaceMemberDto -> Flux.merge(
                               // redis publish
                               redisPublisher.publish(RedisConst.SSE.getChannelTopic(),
                                       toNotificationResponseDto(spaceMemberDto.getUserId(), Notification.EventType.CREATED, Notification.NotificationType.TEXT, response)),
                               // message save
                               saveNotification(spaceMemberDto, response))
                       )
               )
               .then()
               .subscribe();
    }

    /**
     * 웹소켓 세션 시작 시 시작
     * @param joinPoint 세션 시작 메소드
     */
    @After("startSessionPointcut()")
    public void afterStartSession(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        Long channelId = (Long) args[1];

        notificationFacade.deleteNotification(userId, channelId)
                .subscribe();
    }

    private Mono<Void> saveNotification(SpaceMemberDto spaceMemberDto, TextResponse response) {
        return notificationFacade.createNotification(NotificationDto.builder()
                .userId(spaceMemberDto.getUserId())
                .spaceId(spaceMemberDto.getSpaceId())
                .channelId(response.getChannelId())
                .eventType(Notification.EventType.CREATED)
                .notificationType(Notification.NotificationType.TEXT)
                .message(response.getId())
                .build());
    }

    /**
     * redis로 메시지를 발행한다.
     * @param result 메서드 반환 값
     * @param eventType 이벤트 타입(업데이트, 삭제 등)
     */
    private Mono<ChannelResponseDto> messageSend(Mono<ChannelResponseDto> result, Notification.EventType eventType) {
        return result.flatMap(response -> getSpaceMembers(response.getId())
                .flatMap(notification -> redisPublisher.publish(RedisConst.SSE.getChannelTopic(),
                        toNotificationResponseDto(notification.getUserId(), eventType, response)))
                .then(Mono.just(response)));
    }

    private Flux<SpaceMemberDto> getSpaceMembers(Long channelId) {
        return channelFacade.getSpaceMembersByChannelId(channelId);
    }

    private NotificationResponseDto toNotificationResponseDto(Long userId, Notification.EventType eventType, Object data) {
        return NotificationResponseDto.builder()
                .userId(userId)
                .eventType(eventType)
                .notificationType(Notification.NotificationType.CHANNEL)
                .data(data)
                .build();
    }

    private NotificationResponseDto toNotificationResponseDto(Long userId, Notification.EventType eventType, Notification.NotificationType notificationType, Object data) {
        return NotificationResponseDto.builder()
                .userId(userId)
                .eventType(eventType)
                .notificationType(notificationType)
                .data(data)
                .build();
    }

}
