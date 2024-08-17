package com.echo.echo.domain;

import com.echo.echo.common.redis.RedisConst;
import com.echo.echo.common.redis.RedisPublisher;
import com.echo.echo.common.s3.service.S3Service;
import com.echo.echo.common.s3.util.FileUtils;
import com.echo.echo.domain.text.TextService;
import com.echo.echo.domain.text.dto.TextRequest;
import com.echo.echo.domain.text.entity.Text;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TextFacade {

    private final TextService textService;
    private final S3Service s3Service;
    private final RedisPublisher redisPublisher;

    public Mono<Void> textChatFileUpload(Mono<FilePart> filePart, Long userId, String username, Long channelId) {
        return filePart.flatMap(part -> {
            FileUtils.filePartValidator(part);

            Mono<String> uploadUrl = s3Service.upload(part);

            return uploadUrl.flatMap(url -> {
                    Mono<TextRequest> request = Mono.just(new TextRequest(url));

                        return request
                                .flatMap(req -> textService.sendText(request, username, userId, channelId, Text.TextType.FILE))
                                .flatMap(response -> {
                                    ChannelTopic topic = new ChannelTopic(RedisConst.TEXT.name());
                                    return redisPublisher.publish(topic, response);
                                });
                    });
        });
    }

}
