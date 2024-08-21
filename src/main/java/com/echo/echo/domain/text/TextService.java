package com.echo.echo.domain.text;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.text.dto.TextRequest;
import com.echo.echo.domain.text.dto.TextResponse;
import com.echo.echo.domain.text.dto.TypingRequest;
import com.echo.echo.domain.text.dto.TypingResponse;
import com.echo.echo.domain.text.entity.Text;
import com.echo.echo.domain.text.error.TextErrorCode;
import com.echo.echo.domain.text.repository.TextRepository;
import com.echo.echo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextService {

    private final TextRepository repository;
    private final Map<Long, Sinks.Many<TextResponse>> channelSinks = new ConcurrentHashMap<>();

    public Mono<TextResponse> sendText(Mono<TextRequest> request, String username, Long userId, Long channelId, Text.TextType type) {
        return request
                .map(textRequest -> new Text(textRequest.getContents(), username, userId, channelId, type))
                .flatMap(repository::save)
                .map(TextResponse::new);
    }

    public Mono<TextResponse> updateText(TextRequest request, String textId, User user) {
        return repository.findById(textId)
                .doOnNext(text -> {
                    if (!text.getUserId().equals(user.getId())) {
                        throw new CustomException(TextErrorCode.AUTHOR_NOT_MATCHES);
                    }
                })
                .flatMap(text -> {
                    text.updateContents(request);
                    return repository.save(text)
                            .map(TextResponse::new);
                });
    }

    public Mono<Void> deleteText(String textId, User user) {
        return repository.findById(textId)
                .doOnNext(text -> {
                    if (!text.getUserId().equals(user.getId())) {
                        throw new CustomException(TextErrorCode.AUTHOR_NOT_MATCHES);
                    }
                })
                .flatMap(repository::delete);
    }

    public Mono<Void> deleteText(String textId) {
        return repository.deleteById(textId);
    }

    public Sinks.Many<TextResponse> getSink(Long channelId) {
        return channelSinks.compute(channelId, (id, existingSink) -> {
            if (existingSink == null || existingSink.currentSubscriberCount() == 0) {
                return Sinks.many().multicast().onBackpressureBuffer();
            }
            return existingSink;
        });
    }

    public Mono<TypingResponse> sendTyping(Mono<TypingRequest> request, String username, Long channelId) {
        return request
                .map(req -> new TypingResponse(req, username, channelId));
    }

    public Flux<TextResponse> loadTextByChannelId(Long channelId) {
        return repository.findAllByChannelId(channelId)
                .map(TextResponse::new);
    }

    public void startSession(Long userId, Long channelId) {
        log.debug("채팅방 입장, 유저 id {}, channelId {} notification 데이터를 삭제합니다.", userId, channelId);
    }

    public Flux<Text> findTextsByChannelId(Long channelId) {
        return repository.findAllByChannelId(channelId);
    }

}
