package com.echo.echo.domain.text.controller;

import com.echo.echo.domain.text.TextFacade;
import com.echo.echo.domain.text.dto.TextRequest;
import com.echo.echo.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("text")
public class TextController {

    private final TextFacade textFacade;

    @PostMapping("/{channelId}/file")
    public Mono<Void> textChatFileUpload(@PathVariable("channelId") Long channelId,
                                         @RequestPart("file-data") FilePart filePart,
                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        String username = userPrincipal.getUser().getNickname();
        return textFacade.textChatFileUpload(Mono.just(filePart), userId, username, channelId);
    }

    @PutMapping("/{textId}")
    public Mono<Void> updateText(@RequestBody TextRequest request,
                                         @PathVariable("textId") String textId,
                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return textFacade.updateTextChat(request, textId, userPrincipal.getUser());
    }

    @DeleteMapping("/{channelId}/{textId}")
    public Mono<Void> deleteText(@PathVariable("channelId") Long channelId,
                                 @PathVariable("textId") String textId,
                                 @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return textFacade.deleteTextChat(channelId, textId, userPrincipal.getUser());
    }

}
