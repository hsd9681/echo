package com.echo.echo.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import com.echo.echo.domain.text.controller.TextWebSocketHandler;
import com.echo.echo.domain.thread.ThreadWebSocketHandler;
import com.echo.echo.domain.video.VideoHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig {

	@Bean
	public HandlerMapping handlerMapping(TextWebSocketHandler textHandler, VideoHandler videoHandler,
		ThreadWebSocketHandler threadWebSocketHandler) {
		Map<String, WebSocketHandler> map = new HashMap<>();
		map.put("/video/**", videoHandler);
		map.put("/text", textHandler);
		map.put("/threads", threadWebSocketHandler);

		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		mapping.setUrlMap(map);
		mapping.setOrder(1);
		return mapping;
	}

}