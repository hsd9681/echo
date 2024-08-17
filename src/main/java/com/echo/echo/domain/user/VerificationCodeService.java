package com.echo.echo.domain.user;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.common.redis.RedisService;
import com.echo.echo.domain.user.entity.VerificationCode;
import com.echo.echo.domain.user.error.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RequiredArgsConstructor
@Service
@Slf4j
public class VerificationCodeService {

    private final RedisService redisService;

    /**
     * 인증번호 생성
     * @param userId 유저 식별 정보
     * @param type 인증번호 저장 타입
     */
    public Mono<VerificationCode> createVerificationCode(String userId, VerificationCode.Type type) {
        VerificationCode verificationCode = VerificationCode.createVerificationCode(userId, type);
        log.info("verificationCode 생성: {}", verificationCode.getCode());
        return redisService.setValue(verificationCode.getUuid(),
                        verificationCode,
                        Duration.ofMinutes(VerificationCode.TIME_LIMIT))
                .thenReturn(verificationCode);
    }

    /**
     * 인증이 완료되었는지 확인한다.
     * @param uuid key
     */
    public Mono<Void> isVerificationCodeValid(String uuid) {
        return redisService.getCacheValueGeneric(uuid, VerificationCode.class)
                .filter(VerificationCode::isStatus)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(UserErrorCode.VERIFY_BEFORE))))
                .then();
    }

    /**
     * 인증코드가 맞는지 확인하고 맞다면 성공 값으로 변경한다.
     * @param uuid key
     * @param code 확인할 인증 코드
     */
    public Mono<Void> checkVerificationCodeAndUpdate(String uuid, int code) {
        return redisService.getCacheValueGeneric(uuid, VerificationCode.class)
                .filter(v -> code == v.getCode())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(UserErrorCode.INCORRECT_VERIFICATION_NUMBER))))
                .then(Mono.defer(() -> updateVerificationSuccess(uuid)));
    }

    /**
     * 인증코드를 삭제한다.
     * 인증번호 확인 및 모든 처리 완료 후 진행
     * @param uuid key
     */
    public Mono<Void> deleteVerificationCode(String uuid) {
        return redisService.deleteValue(uuid)
                .doOnError(err -> Mono.error(new CustomException(UserErrorCode.VERIFICATION_ID_NOT_MATCH)));
    }

    /**
     * 인증번호 상태 값을 성공한 값으로 변경
     * @param uuid key
     */
    private Mono<Void> updateVerificationSuccess(String uuid) {
        return redisService.getCacheValueGeneric(uuid, VerificationCode.class)
                .filter(v -> !v.isStatus())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(UserErrorCode.VERIFICATION_ALREADY_COMPLETED))))
                .map(VerificationCode::updateSuccess)
                .flatMap(updateCode -> redisService.setValue(uuid, updateCode))
                .then();
    }

}
