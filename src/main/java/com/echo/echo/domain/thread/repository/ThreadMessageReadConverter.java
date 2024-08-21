package com.echo.echo.domain.thread.repository;

import com.echo.echo.domain.thread.entity.Thread;
import com.echo.echo.domain.thread.entity.ThreadMessage;
import com.echo.echo.domain.user.entity.User;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDateTime;

@ReadingConverter
public class ThreadMessageReadConverter implements Converter<Row, ThreadMessage> {

    @Override
    public ThreadMessage convert(Row source) {
        User user = User.builder()
                .id(source.get("author_id", Long.class))
                .email(source.get("email", String.class))
                .nickname(source.get("nickname", String.class))
                .build();

        Thread thread = Thread.builder()
                .id(source.get("thread_id", Long.class))
                .textId(source.get("text_id", String.class))
                .creatorId(source.get("author_id", Long.class))
                .status(getStatus(source.get("status", int.class)))
                .build();

        return ThreadMessage.builder()
                .id(source.get("id", Long.class))
                .threadId(source.get("thread_id", Long.class))
                .authorId(source.get("author_id", Long.class))
                .content(source.get("content", String.class))
                .user(user)
                .thread(thread)
                .createdAt(source.get("created_at", LocalDateTime.class))
                .modifiedAt(source.get("modified_at", LocalDateTime.class))
                .build();
    }

    private Thread.Status getStatus(int ordinal) {
        return Thread.Status.values()[ordinal];
    }

}
