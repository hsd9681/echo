package com.echo.echo.domain.user;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.user.dto.*;
import com.echo.echo.domain.user.entity.User;
import com.echo.echo.domain.user.entity.VerificationCode;
import com.echo.echo.domain.user.error.UserErrorCode;
import com.echo.echo.domain.user.error.UserSuccessCode;
import com.echo.echo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;

    /**
     * 인증코드 확인이 된 사용자만 회원가입 가능
     * @param req 회원가입 정보
     * @return 회원가입한 유저 정보
     */
    protected Mono<UserResponseDto> signup(UserRequestDto req) {

        return isVerificationCodeValid(req.getEmail())
                .then(Mono.defer(() -> checkDuplicateEmail(req.getEmail()))
                        .then(Mono.just(User.builder()
                                .email(req.getEmail())
                                .password(passwordEncoder.encode(req.getPassword()))
                                .intro(req.getIntro())
                                .nickname(req.getNickname())
                                .build())
                        )
                )
                .flatMap(userRepository::save)
                .flatMap(user -> verificationCodeService.deleteVerificationCode(req.getEmail()).thenReturn(user))
                .map(UserResponseDto::new);
    }

    /**
     * 회원가입 시 이메일 유효성 검사 인증번호를 생성한다
     */
    public Mono<Integer> createEmailVerificationCode(String email) {
        return verificationCodeService.createVerificationCode(email, VerificationCode.Type.SIGNUP)
                .map(VerificationCode::getCode);
    }

    /**
     * 인증코드 확인 및 유저 활성화
     * @param email 유저 이메일
     * @return 인증 확인 내용
     */
    public Mono<Void> isVerificationCodeValid(String email) {
        // 활성화 여부 확인 후 유저 활성화 진행
        return verificationCodeService.isVerificationCodeValid(email);
    }

    /**
     * 인증코드 확인
     * @param uuid 인증코드 id
     * @param code 인증코드
     */
    public Mono<String> checkVerificationCode(String uuid, int code) {
        return verificationCodeService.checkVerificationCodeAndUpdate(uuid, code)
                .thenReturn(UserSuccessCode.VERIFICATION_SUCCESS.getMsg());
    }

    /**
     * 이메일 찾기
     * @param email 찾을 이메일 정보
     * @return 확인한 내용
     */
    public Mono<FindUserResponseDto> findUserId(String email) {
        return existsByEmail(email)
                .map(isFind -> {
                    String message = isFind ? UserSuccessCode.FOUND_USER.getMsg() : UserSuccessCode.NOT_FOUND_USER.getMsg();
                    return FindUserResponseDto.builder()
                            .isFind(isFind)
                            .msg(message)
                            .build();
                });
    }

    /**
     * 비밀번호 찾기 인증을 위해 인증번호를 발급한다.
     * @param email 발급할 이메일
     * @return 발급된 인증번호 정보
     */
    public Mono<VerificationCode> findPassword(String email) {
        return existsByEmail(email)
                .filter(isFInd -> isFInd)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(UserErrorCode.USER_NOT_FOUND))))
                .then(verificationCodeService.createVerificationCode(email, VerificationCode.Type.CHANGE));
    }

    /**
     * 유저 정보를 업데이트한다.
     * @param userId 변경할 유저 아이디
     * @param req 변경할 유저 정보
     */
    public Mono<User> updateProfile(Long userId, UpdateProfileRequestDto req) {
        return findById(userId)
            .flatMap(user -> {
                user.updateUsername(req.getNickname());
                user.updateIntro(req.getIntro());
                return userRepository.save(user);
            });
    }

    /**
     * 로그인된 사용자의 비밀번호를 변경한다.
     * @param userId 변경할 유저 아이디
     * @param req 현재, 변경할 비밀번호 정보
     */
    public Mono<Void> changePassword(Long userId, ChangePasswordRequestDto req) {
        return findById(userId)
            .flatMap(user -> {
                if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                    return Mono.error(new CustomException(UserErrorCode.PASSWORD_NOT_MATCH));
                }
                user.updatePassword(passwordEncoder.encode(req.getNewPassword()));
                return userRepository.save(user);
            })
            .then();
    }

    /**
     * 인증코드 확인 후 비밀번호 변경 진행
     * 확인 완료된 인증코드 데이터는 삭제한다.
     * @param uuid 인증코드 uuid
     * @param req 변경할 비밀번호 정보 및 이메일 정보
     */
    public Mono<Void> checkVerificationCodeAndChangePassword(String uuid, FindUserDto.Password req) {
        return verificationCodeService.isVerificationCodeValid(uuid)
                .then(Mono.defer(() -> changePassword(req.getEmail(), req.getNewPassword())))
                .then(Mono.defer(() -> verificationCodeService.deleteVerificationCode(uuid)));
    }

    /**
     * 비로그인 사용자의 비밀번호를 변경한다.
     * @param email 변경할 유저 이메일
     * @param newPassword 변경할 비밀번호 정보
     */
    public Mono<Void> changePassword(String email, String newPassword) {
        return findByEmail(email)
                .flatMap(user -> {
                    user.updatePassword(passwordEncoder.encode(newPassword));
                    return userRepository.save(user);
                })
                .then();
    }

    /**
     * 여러 id의 유저 정보를 가져온다.
     * @param ids 조회할 id List
     */
    public Flux<User> findByIdIn(List<Long> ids) {
        return userRepository.findByIdIn(ids);
    }

    /**
     * 회원가입 시 유저 이메일이 중복인지 확인한다.
     * @param email 확인할 이메일
     */
    protected Mono<Void> checkDuplicateEmail(String email) {
        return existsByEmail(email)
                .filter(isDuplicated -> !isDuplicated)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(UserErrorCode.ALREADY_EXIST_EMAIL))))
                .then();
    }

    /**
     * id로 유저 정보를 가져온다.
     * @param id 유저 id
     * @return 해당 유저 정보
     */
    public Mono<User> findById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(UserErrorCode.USER_NOT_FOUND))));
    }

    /**
     * 이메일을 기준으로 유저 정보를 가져온다.
     * @param email 유저 이메일
     * @return 유저 정보
     */
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(UserErrorCode.USER_NOT_FOUND))));
    }

    /**
     * 이메일 존재 여부 확인
     * @param email 확인할 이메일
     */
    protected Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
