package com.echo.echo.domain.channel;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.domain.channel.error.ChannelErrorCode;
import com.echo.echo.domain.channel.dto.ChannelRequestDto;
import com.echo.echo.domain.channel.dto.ChannelResponseDto;
import com.echo.echo.domain.channel.entity.Channel;
import com.echo.echo.domain.channel.repository.ChannelRepository;
import com.echo.echo.domain.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;

/**
 * ChannelService는 채널 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */

@RequiredArgsConstructor
@Service
public class ChannelService {

	private final ChannelRepository channelRepository;
	private final TransactionalOperator transactionalOperator;

	/**
	 * 새로운 채널을 생성합니다.
	 *
	 * @param spaceId 채널이 속한 스페이스 ID
	 * @param requestDto 채널 생성 요청 정보
	 * @return 생성된 채널의 응답 DTO
	 */
	public Mono<ChannelResponseDto> createChannel(Long spaceId, ChannelRequestDto requestDto) {
		Channel channel = buildChannel(null, spaceId, requestDto);
		return channelRepository.save(channel)
			.map(this::toChannelResponseDto);
	}

	/**
	 * 특정 스페이스의 채널 목록을 조회합니다.
	 *
	 * @param spaceId 조회할 스페이스 ID
	 * @param pushMessage 사용자에게 전송된 푸시 메시지 정보
	 * @return 채널 목록 응답 DTO
	 */
	public Flux<ChannelResponseDto> getChannels(Long spaceId, Map<Long, Notification> pushMessage) {
		return channelRepository.findBySpaceId(spaceId)
			.map(channel -> {
				if (pushMessage.containsKey(channel.getId())) {
					return toChannelResponseDto(channel, true, pushMessage.get(channel.getId()).getMessage());
				}
				return toChannelResponseDto(channel, false, null);
			});
	}

	/**
	 * ID를 통해 특정 채널을 조회합니다.
	 *
	 * @param channelId 조회할 채널 ID
	 * @param pushMessage 해당 채널에 대한 푸시 메시지 정보
	 * @return 조회된 채널의 응답 DTO
	 */
	public Mono<ChannelResponseDto> getChannel(Long channelId, Notification pushMessage) {
		return channelRepository.findById(channelId)
			.map(channel -> {
				if (pushMessage != null) {
					return toChannelResponseDto(channel, true, pushMessage.getMessage());
				}
				return toChannelResponseDto(channel, false, null);
			});
	}

	/**
	 * 기존 채널의 정보를 업데이트합니다.
	 *
	 * @param channelId 업데이트할 채널 ID
	 * @param requestDto 업데이트할 채널 정보
	 * @return 업데이트된 채널의 응답 DTO
	 */
	public Mono<ChannelResponseDto> updateChannel(Long channelId, ChannelRequestDto requestDto) {
		return findChannelById(channelId)
			.flatMap(channel -> {
				Channel updatedChannel = buildChannel(channelId, channel.getSpaceId(), requestDto);
				return channelRepository.save(updatedChannel);
			})
			.map(this::toChannelResponseDto);
	}

	/**
	 * 특정 채널을 삭제합니다.
	 *
	 * @param channelId 삭제할 채널 ID
	 * @return 삭제 작업 완료 후 처리
	 */
	public Mono<Void> deleteChannel(Long channelId) {
		return findChannelById(channelId)
			.flatMap(channelRepository::delete);
	}

	/**
	 * 채널의 현재 멤버 수를 증가시키고, 최대 멤버 수 초과 여부를 확인합니다.
	 *
	 * @param channelId 멤버 수를 증가시킬 채널 ID
	 * @return 멤버 수가 증가된 채널의 응답 DTO
	 */
	public Mono<ChannelResponseDto> checkAndIncrementMemberCount(Long channelId) {
		return channelRepository.findById(channelId)
			.flatMap(channel -> channel.incrementMemberCount()
				.flatMap(channelRepository::save))
			.as(transactionalOperator::transactional)
			.map(this::toChannelResponseDto);
	}

	/**
	 * 채널의 현재 멤버 수를 감소시킵니다.
	 *
	 * @param channelId 멤버 수를 감소시킬 채널 ID
	 * @return 멤버 수가 감소된 채널의 응답 DTO
	 */
	public Mono<ChannelResponseDto> decrementMemberCount(Long channelId) {
		return channelRepository.findById(channelId)
			.flatMap(channel -> channel.decrementMemberCount()
				.flatMap(channelRepository::save))
			.as(transactionalOperator::transactional)
			.map(this::toChannelResponseDto);
	}

	/**
	 * ID로 특정 채널을 조회합니다.
	 *
	 * @param channelId 조회할 채널 ID
	 * @return 조회된 채널 엔티티
	 */
	protected Mono<Channel> findChannelById(Long channelId) {
		return channelRepository.findById(channelId)
			.switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(ChannelErrorCode.CHANNEL_NOT_FOUND))));
	}

	/**
	 * Channel 엔티티를 생성합니다.
	 *
	 * @param channelId 채널 ID
	 * @param spaceId 채널이 속한 스페이스 ID
	 * @param requestDto 채널 요청 정보
	 * @return 생성된 채널 엔티티
	 */
	private Channel buildChannel(Long channelId, Long spaceId, ChannelRequestDto requestDto) {
		return Channel.builder()
			.id(channelId)
			.spaceId(spaceId)
			.channelName(requestDto.getChannelName())
			.channelType(Channel.Type.valueOf(requestDto.getChannelType()).name())
			.maxCapacity(requestDto.getMaxCapacity())
			.build();
	}

	/**
	 * Channel 엔티티를 ChannelResponseDto로 변환합니다.
	 *
	 * @param channel 변환할 채널 엔티티
	 * @return 변환된 채널 응답 DTO
	 */
	private ChannelResponseDto toChannelResponseDto(Channel channel) {
		return ChannelResponseDto.builder()
			.id(channel.getId())
			.channelName(channel.getChannelName())
			.channelType(channel.getChannelType())
			.maxCapacity(channel.getMaxCapacity())
			.currentMemberCount(channel.getCurrentMemberCount())
			.build();
	}

	/**
	 * Channel 엔티티를 ChannelResponseDto로 변환하며 푸시 알림 상태를 추가합니다.
	 *
	 * @param channel 변환할 채널 엔티티
	 * @param isPush 푸시 알림 활성화 여부
	 * @param lastReadMessageId 마지막으로 읽은 메시지 ID
	 * @return 변환된 채널 응답 DTO
	 */
	private ChannelResponseDto toChannelResponseDto(Channel channel, boolean isPush, String lastReadMessageId) {
		return ChannelResponseDto.builder()
			.id(channel.getId())
			.channelName(channel.getChannelName())
			.channelType(channel.getChannelType())
			.maxCapacity(channel.getMaxCapacity())
			.currentMemberCount(channel.getCurrentMemberCount())
			.push(isPush)
			.lastReadMessageId(lastReadMessageId)
			.build();
	}

}
