package com.echo.echo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder clientBuilder) {
        return clientBuilder
                .baseUrl("https://kauth.kakao.com").build();

    }
}