package com.echo.echo.domain.channel;

import com.echo.echo.domain.channel.entity.Channel;
import com.echo.echo.domain.channel.repository.ChannelRepository;
import com.echo.echo.domain.space.entity.Space;
import com.echo.echo.domain.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final SpaceRepository spaceRepository;

    public Mono<Channel> createChannel(Long spaceId, String channelType) {

        return findSpaceById(spaceId)
                .flatMap(space -> {
                    Channel channel = Channel.builder()
                            .channelName("test") // 이름 어떻게 불러올까
                            .space(space)
                            .channelType(Channel.Type.valueOf(channelType))
                            .build();
                    return channelRepository.save(channel);
                });
    }

    public Flux<Channel> getChannel(Long spaceId, String channelType) {
        return channelRepository.findByChannelType(Channel.Type.V.name()); //어디 스페이스에 있는 T와V를 모두 가져와야함
    }

    public Mono<Void> deleteChannel(Long channelId) {
        return channelRepository.deleteById(channelId);
    }

    private Mono<Space> findSpaceById(Long spaceId) { //코드 리펙토링

        return spaceRepository.findById(spaceId)
                .switchIfEmpty(Mono.error(new RuntimeException("Space not found")));
    }

    public Mono<Channel> updateChannel(Long channelId, Long spaceId) {
        return null;
    }
}
