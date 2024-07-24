package com.echo.echo.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    JwtService jwtService;

    @Test
    void createToken() {
        String email = "test@test.com";
        String token = jwtService.createToken(email);
        System.out.println(token);
    }
}