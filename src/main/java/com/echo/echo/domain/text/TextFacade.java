package com.echo.echo.domain.text;

import com.echo.echo.common.redis.RedisConst;
import com.echo.echo.common.redis.RedisPublisher;
import com.echo.echo.common.s3.service.S3Service;
import com.echo.echo.common.s3.util.FileUtils;
import com.echo.echo.domain.text.dto.TextRequest;
import com.echo.echo.domain.text.dto.TextResponse;
import com.echo.echo.domain.text.entity.Text;
import com.echo.echo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
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
                        .flatMap(response -> redisPublisher.publish(RedisConst.TEXT.getChannelTopic(), response));
            });
        });
    }

    public Mono<Void> updateTextChat(TextRequest request, String textId, User user) {
        return textService.updateText(request, textId, user)
                .flatMap(response -> {
                    response.setHandleType(TextResponse.HandleType.UPDATED);
                    return redisPublisher.publish(RedisConst.TEXT.getChannelTopic(), response);
                });
    }

    public Mono<Void> deleteTextChat(Long channelId, String textId, User user) {
        return textService.deleteText(textId, user)
                .then(Mono.defer(() -> {
                    TextResponse response = TextResponse.builder()
                            .id(textId)
                            .channelId(channelId)
                            .handleType(TextResponse.HandleType.DELETED)
                            .build();
                    return redisPublisher.publish(RedisConst.TEXT.getChannelTopic(), response);
                }));
    }

}
