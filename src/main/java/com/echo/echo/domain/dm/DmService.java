package com.echo.echo.domain.dm;

import com.echo.echo.domain.dm.dto.DmResponse;
import com.echo.echo.domain.dm.entity.Dm;
import com.echo.echo.domain.dm.repository.DmRepository;
import com.echo.echo.domain.text.dto.TextRequest;
import com.echo.echo.domain.text.dto.TextResponse;
import com.echo.echo.domain.text.entity.Text;
import com.echo.echo.domain.text.repository.TextRepository;
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
public class DmService {
    private final TextRepository repository;
    private final DmRepository dmRepository;
    private final Map<String, Sinks.Many<TextResponse>> dmSinks = new ConcurrentHashMap<>(); // DM 용

    public Mono<DmResponse> saveDm(Dm dm) {
        return dmRepository.save(dm)
                .map(DmResponse::new)
                .doOnSuccess(response -> log.debug("DM 생성 성공: {}", response))
                .doOnError(error -> log.error("DM 생성 실패", error));
    }

    // 전체 DM 조회
    public Flux<DmResponse> getAllDms(Long userId) {
        return dmRepository.findAllBySenderIdOrReceiverId(userId, userId)
                .map(DmResponse::new)
                .doOnSubscribe(subscription -> log.debug("전체 DM 조회 시작"))
                .doOnError(error -> log.error("전체 DM 조회 실패", error));
    }

    // DM 삭제
    public Mono<Void> deleteDmById(String id) {
        return dmRepository.deleteById(id)
                .doOnSuccess(aVoid -> log.debug("DM 삭제 성공: {}", id))
                .doOnError(error -> log.error("DM 삭제 실패", error));
    }
    public Sinks.Many<TextResponse> getDmSink(String dmId) {
        return dmSinks.compute(dmId, (id, existingSink) -> {
            if (existingSink == null || existingSink.currentSubscriberCount() == 0) {
                return Sinks.many().multicast().onBackpressureBuffer();
            }
            return existingSink;
        });
    }
    public Mono<TextResponse> sendTextToDm(Mono<TextRequest> request, String username, Long userId, String dmId, Text.TextType type) {
        return request
                .map(textRequest -> new Text(textRequest, username, userId, dmId, type))
                .flatMap(repository::save)
                .map(TextResponse::new);
    }
    public Flux<TextResponse> loadTextByDmId(String dmId) {
        return repository.findAllByDmId(dmId)
                .map(TextResponse::new);
    }

}


