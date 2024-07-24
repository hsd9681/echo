package com.echo.echo.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .exceptionHandling(
                        exceptionHandlingSpec -> exceptionHandlingSpec.authenticationEntryPoint(serverAuthenticationEntryPoint())
                )
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers(HttpMethod.GET,"/api/auth").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/users/signup").permitAll()
                        .anyExchange().authenticated()
                )
                ;

        return http.build();
    }

    private ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
        return (exchange, authEx) -> {
            String requestPath = exchange.getRequest().getURI().getPath();

            log.error("Unauthorized error: {}", authEx.getMessage());
            log.error("Requested path    : {}", requestPath);

            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);

            try {
                byte[] errorByte = new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .writeValueAsBytes("로그인이 되어있지 않거나 토큰이 만료되었습니다.");
                DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(errorByte);
                return serverHttpResponse.writeWith(Mono.just(dataBuffer));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                return serverHttpResponse.setComplete();
            }
        };
    }
}
