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

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "thread")
@Getter
public class Thread extends TimeStamp {
    @Id
    private Long id;
    private String textId;
    private int status;
    private Long creatorId;
    public enum Status {
        CLOSE, OPEN
    }

    @Transient
    private User user;

    @Builder
    public Thread(Long id, String textId, Status status, Long creatorId, User user, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.textId = textId;
        this.status = status == null? Status.OPEN.ordinal() : status.ordinal();
        this.creatorId = creatorId;
        this.user = user;
        setCreatedAt(createdAt);
        setModifiedAt(modifiedAt);
    }

    public void openThread() {
        this.status = Status.OPEN.ordinal();
    }

    public void closeThread() {
        this.status = Status.CLOSE.ordinal();
    }
}
