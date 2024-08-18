package com.echo.echo.domain.thread.entity;

import com.echo.echo.common.TimeStamp;
import com.echo.echo.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "thread_message")
@Getter
public class ThreadMessage extends TimeStamp {

    @Id
    private Long id;

    private Long threadId;
    private Long authorId;
    private String content;

    @Transient
    private User user;

    @Transient
    private Thread thread;

    @Builder
    public ThreadMessage(Long id, Long threadId, Long authorId, String content, User user, Thread thread, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.threadId = threadId;
        this.authorId = authorId;
        this.content = content;
        this.user = user;
        this.thread = thread;
        setCreatedAt(createdAt);
        setModifiedAt(modifiedAt);
    }

    public void updateMessage(String content) {
        this.content = content;
    }

}
