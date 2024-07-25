package com.echo.echo.domain.user;

import com.echo.echo.domain.user.dto.UserRequestDto;
import com.echo.echo.domain.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;

    public Mono<UserResponseDto> signup(UserRequestDto userRequestDto) {
        return userService.signup(userRequestDto)
                .map(UserResponseDto::new);
    }
}
