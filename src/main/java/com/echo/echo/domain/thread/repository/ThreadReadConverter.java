package com.echo.echo.domain.thread.repository;

import com.echo.echo.domain.thread.entity.Thread;
import com.echo.echo.domain.user.entity.User;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDateTime;

@ReadingConverter
public class ThreadReadConverter implements Converter<Row, Thread> {
    @Override
    public Thread convert(Row source) {
        User user = User.builder()
                .id(source.get("creator_id", Long.class))
                .email(source.get("email", String.class))
                .nickname(source.get("nickname", String.class))
                .build();

        return Thread.builder()
                .id(source.get("id", Long.class))
                .channelId(source.get("channel_id", Long.class))
                .textId(source.get("text_id", String.class))
                .status(getStatus(source.get("status", int.class)))
                .user(user)
                .createdAt(source.get("created_at", LocalDateTime.class))
                .modifiedAt(source.get("modified_at", LocalDateTime.class))
                .build();
    }

    private Thread.Status getStatus(int ordinal) {
        return Thread.Status.values()[ordinal];
    }
}
