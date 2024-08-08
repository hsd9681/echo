package com.echo.echo.config;

import com.echo.echo.common.redis.RedisPublisher;
import com.echo.echo.common.util.ObjectStringConverter;
import com.echo.echo.domain.text.TextService;
import com.echo.echo.domain.text.controller.TextWebSocketHandler;
import com.echo.echo.domain.video.VideoHandler;
import com.echo.echo.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig {

    @Bean
    public TextWebSocketHandler textWebSocketHandler(JwtProvider jwtProvider,
                                                     TextService textService,
                                                     ObjectStringConverter objectStringConverter,
                                                     RedisPublisher redisPublisher) {
        return new TextWebSocketHandler(jwtProvider, textService, objectStringConverter, redisPublisher);
    }

    @Bean
    public HandlerMapping handlerMapping(TextWebSocketHandler textHandler, VideoHandler videoHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/video/**", videoHandler);
        map.put("/text", textHandler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(1);
        return mapping;
    }
}