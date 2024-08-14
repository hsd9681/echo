package com.echo.echo.domain.dm;

import com.echo.echo.domain.dm.dto.DmResponse;
import com.echo.echo.domain.dm.entity.Dm;
import com.echo.echo.domain.dm.repository.DmRepository;
import com.echo.echo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DmService {

    private final DmRepository dmRepository;
    private final UserRepository userRepository;  // UserRepository 주입

    // DM 생성
    public Mono<DmResponse> createDm(String recipientEmail) {
        return userRepository.findByEmail(recipientEmail)
                .flatMap(user -> {
                    if (user != null) {
                        Dm dm = new Dm();  // 기본 생성자를 사용하여 객체를 생성
                        dm.setNickname(user.getNickname());  // 닉네임만 설정

                        return dmRepository.save(dm)
                                .map(DmResponse::new)
                                .doOnSuccess(response -> log.info("DM 생성 성공: {}", response))
                                .doOnError(error -> log.error("DM 생성 실패", error));
                    } else {
                        return Mono.error(new RuntimeException("사용자가 존재하지 않습니다: " + recipientEmail));
                    }
                })
                .doOnError(error -> log.error("DM 생성 실패", error));
    }


    // 전체 DM 조회
    public Flux<DmResponse> getAllDms() {
        return dmRepository.findAll()
                .map(DmResponse::new)
                .doOnSubscribe(subscription -> log.info("전체 DM 조회 시작"))
                .doOnError(error -> log.error("전체 DM 조회 실패", error));
    }
    // DM 삭제
    public Mono<Void> deleteDmById(String id) {
        return dmRepository.deleteById(id)
                .doOnSuccess(aVoid -> log.info("DM 삭제 성공: {}", id))
                .doOnError(error -> log.error("DM 삭제 실패", error));
    }

    private String generateDmId(String recipientEmail) {
        return recipientEmail + "_dm";  // 예시로 사용된 간단한 DM ID 생성 방법
    }
}
