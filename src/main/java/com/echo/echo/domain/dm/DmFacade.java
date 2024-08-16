package com.echo.echo.domain.dm;

import com.echo.echo.domain.dm.dto.DmResponse;
import com.echo.echo.domain.dm.entity.Dm;
import com.echo.echo.domain.user.UserService;
import com.echo.echo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DmFacade {

    private final DmService dmService;
    private final UserService userService;

    public Mono<DmResponse> createDm(String recipientEmail, User sender) {
        return userService.findByEmail(recipientEmail)
                .flatMap(user -> {
                    Dm dm = new Dm(sender.getId(), sender.getNickname(), user.getId(), user.getNickname());  // 기본 생성자를 사용하여 객체를 생성

                    return dmService.saveDm(dm);
                });
    }
}
