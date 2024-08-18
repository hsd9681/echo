package com.echo.echo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {

        var partReader = new DefaultPartHttpMessageReader();
        partReader.setMaxParts(4);

        partReader.setMaxDiskUsagePerPart(20L * 1024L * 1024L); // 최대 업로드 허용크기, 현재 20MB로 설정
        partReader.setEnableLoggingRequestDetails(true);
        MultipartHttpMessageReader multipartReader = new MultipartHttpMessageReader(partReader);
        multipartReader.setEnableLoggingRequestDetails(true);
        configurer.defaultCodecs().multipartReader(multipartReader);

        configurer.defaultCodecs().maxInMemorySize(512 * 1024);
    }

}
