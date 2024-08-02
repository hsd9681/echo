package com.echo.echo.domain.user.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Random;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class VerificationCode {
    public static int TIME_LIMIT = 10;
    private String uuid;
    private String userId;
    private int type;
    private int code;
    private boolean status;

    public enum Type {
        SIGNUP, CHANGE
    }

    private VerificationCode(String userId, Type type) {
        this.uuid = type != Type.CHANGE? userId : UUID.randomUUID().toString();
        this.userId = userId;
        this.code = new Random(System.currentTimeMillis()).nextInt(900000) + 100000;
        this.type = type.ordinal();
        this.status = false;
    }

    public static VerificationCode createVerificationCode(String userId, Type type) {
        return new VerificationCode(userId, type);
    }

    public VerificationCode updateSuccess() {
        this.status = true;
        return this;
    }
}
