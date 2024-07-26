package com.echo.echo.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtProviderTest {

    @Autowired
    JwtProvider jwtProvider;

    @Test
    void createAccessToken() {
        Long id = 1L;
        String email = "test@test.com";
        String token = jwtProvider.createAccessToken(id, email);
        System.out.println(token);
    }
}