package com.echo.echo.security.config;

import com.echo.echo.security.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JwtService jwtService) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .exceptionHandling(
                        exceptionHandlingSpec -> exceptionHandlingSpec
                                .authenticationEntryPoint((exchange, ex) -> {
                                    log.error("Authentication error: Unauthorized[401]: " + ex.getMessage());
                                    return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                                })
                                .accessDeniedHandler((exchange, denied) -> {
                                    log.error("Authentication error: Access Denied[403]: " + denied.getMessage());
                                    return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                                })
                )

                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers(HttpMethod.GET,"/api/auth").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/users/signup").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                ;

        return http.build();
    }

    private AuthenticationWebFilter authenticationWebFilter() {
        ReactiveAuthenticationManager authenticationManager = Mono::just;
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter());
        return authenticationWebFilter;
    }

    private ServerAuthenticationConverter serverAuthenticationConverter() {
        return exchange -> {
            String token = jwtService.resolveToken(exchange.getRequest());
            if (StringUtils.hasText(token) && jwtService.isValidToken(token)) {
                return Mono.justOrEmpty(jwtService.getAuthentication(token));
            }
            return Mono.empty();
        };
    }
}
