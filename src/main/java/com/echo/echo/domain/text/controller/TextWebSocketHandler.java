package com.echo.echo.domain.text.controller;

import com.echo.echo.common.redis.RedisConst;
import com.echo.echo.common.redis.RedisPublisher;
import com.echo.echo.common.util.ObjectStringConverter;
import com.echo.echo.domain.channel.ChannelService;
import com.echo.echo.domain.dm.DmService;
import com.echo.echo.domain.text.TextService;
import com.echo.echo.domain.text.dto.TextRequest;
import com.echo.echo.domain.text.dto.TextResponse;
import com.echo.echo.domain.text.dto.TypingRequest;
import com.echo.echo.domain.text.dto.TypingResponse;
import com.echo.echo.domain.text.entity.Text;
import com.echo.echo.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "textHandler")
@RequiredArgsConstructor
@Component
public class TextWebSocketHandler implements WebSocketHandler {

	private final JwtProvider jwtProvider;
	private final TextService textService;
	private final ChannelService channelService;
	private final DmService dmService;
	private final ObjectStringConverter objectStringConverter;
	private final RedisPublisher redisPublisher;

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		Map<String, String> uriQuery = getParamFromSession(session);
		Long channelId = uriQuery.get("channel") == null ? null : Long.valueOf(uriQuery.get("channel"));

		String dmId = uriQuery.get("dmId"); // DM 관련 파라미터
		String token = uriQuery.get("token");

		String username = jwtProvider.getNickName(token);
		Long userId = jwtProvider.getUserId(token);

		return channelService.checkAndIncrementMemberCount(channelId)
			.then(Mono.defer(() -> {
				Sinks.Many<TextResponse> textResponseSink = dmId != null ?
					dmService.getDmSink(dmId) : textService.getSink(channelId);

				textService.startSession(userId, channelId);

				Flux<TextResponse> textResponseFlux = textResponseSink.asFlux();

				Flux<WebSocketMessage> sendMessagesFlux = textResponseFlux
					.flatMap(objectStringConverter::objectToString)
					.map(session::textMessage)
					.doOnError(throwable -> log.error("웹소켓 메시지 변환 간 오류 발생", throwable));

				Mono<Void> output = session.send(sendMessagesFlux);

				Mono<Void> input = session.receive()
					.map(WebSocketMessage::getPayloadAsText)
					.flatMap(payload -> {
						if (payload.contains("$p&ing")) {
							return Mono.empty();
						}
						if (payload.contains("typing") && channelId != null) {
							Mono<TypingRequest> request = objectStringConverter.stringToObject(payload,
								TypingRequest.class);
							return textService.sendTyping(request, username, channelId)
								.flatMap(response -> {
									ChannelTopic topic = new ChannelTopic(RedisConst.TYPING.name());
									return redisPublisher.publish(topic, response);
								});
						} else {
							Mono<TextRequest> request = objectStringConverter.stringToObject(payload,
								TextRequest.class);
							return (dmId != null ?
								dmService.sendTextToDm(request, username, userId, dmId, Text.TextType.TEXT) :
								textService.sendText(request, username, userId, channelId, Text.TextType.TEXT))
								.flatMap(response -> {
									response.setHandleType(TextResponse.HandleType.CREATED);
									return redisPublisher.publish(RedisConst.TEXT.getChannelTopic(), response);
								});
						}
					})
					.doOnSubscribe(subscription -> {
						(dmId != null ?
							dmService.loadTextByDmId(dmId) :
							textService.loadTextByChannelId(channelId))
							.flatMap(objectStringConverter::objectToString)
							.map(session::textMessage)
							.flatMap(messages -> session.send(Mono.just(messages)))
							.then()
							.subscribe();
					}).then()
					.doOnError(e -> {
						session.close();
					})
					.doFinally(signal -> {
						channelService.decrementMemberCount(channelId).subscribe();
						session.close();
					});

				return Mono.when(input, output);
			}))
			.onErrorResume(e -> {
				log.error(e.getMessage());
				WebSocketMessage errorMessage = session.textMessage("{\"msg\": \"" + e.getMessage() + "\"}");
				return session.send(Mono.just(errorMessage))
					.then(session.close());
			});
	}

	private Map<String, String> getParamFromSession(WebSocketSession session) {
		String query = session.getHandshakeInfo().getUri().getQuery();
		Map<String, String> queryParam = new HashMap<>();

		String[] pairs = query.split("&");

		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			String key = keyValue[0];
			String value = keyValue.length > 1 ? keyValue[1] : "";
			queryParam.put(key, value);
		}

		return queryParam;
	}

	public Mono<Sinks.EmitResult> sendTyping(String body) {
		return Mono.fromSupplier(() -> objectStringConverter.stringToObject(body, TypingResponse.class))
			.flatMap(response -> response.map(res ->
				textService.getSink(res.getChannelId()).tryEmitNext(res)))
			.doOnSuccess(emitResult -> {
				if (emitResult.isFailure()) {
					log.error("타이핑 상태 전송 실패: {}", body);
				}
			});
	}

	public Mono<Sinks.EmitResult> sendText(String body) {
		return Mono.fromSupplier(() -> objectStringConverter.stringToObject(body, TextResponse.class))
			.flatMap(response -> response.map(res ->
				(res.getDmId() != null ?
					dmService.getDmSink(res.getDmId()) :
					textService.getSink(res.getChannelId())).tryEmitNext(res))) // DM ID 또는 채널 ID 사용
			.doOnSuccess(emitResult -> {
				if (emitResult.isFailure()) {
					log.error("Redis Sub 메시지 전송 실패: {}", body);
				}
			});
	}

}
