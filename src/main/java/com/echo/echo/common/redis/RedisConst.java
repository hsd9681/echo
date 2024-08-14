package com.echo.echo.common.redis;

import lombok.Getter;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;

@Getter
public enum RedisConst {
    TEXT,
    TYPING,
    THREAD,
    ;

    public ChannelTopic getChannelTopic() {
        return new ChannelTopic(this.name());
    }

    public PatternTopic getPatternTopic(String pattern) {
        String patternTopic = this.name() + pattern;
        return new PatternTopic(patternTopic);
    }
}
