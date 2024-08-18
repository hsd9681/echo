package com.echo.echo.domain.text.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class TypingResponse extends TextResponse {

	private Long channelId;
	private String username;
	private boolean typing;

	public  TypingResponse(TypingRequest request, String username, Long channelId) {
		this.channelId = channelId;
		this.username = username;
		this.typing = request.isTyping();
	}

	@JsonCreator
	public TypingResponse(@JsonProperty("channelId") Long channelId,
		                  @JsonProperty("username") String username,
		                  @JsonProperty("typing") boolean typing) {
		this.channelId = channelId;
		this.username = username;
		this.typing = typing;
	}

}