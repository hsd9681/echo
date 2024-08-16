package com.echo.echo.domain.dm.dto;

import com.echo.echo.domain.dm.entity.Dm;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DmResponse {

    private String id;
    private Long senderId;
    private String sender;
    private Long receiverId;
    private String receiver;

    public DmResponse(Dm dm) {
        this.id = dm.getId();
        this.senderId = dm.getSenderId();
        this.sender = dm.getSender();
        this.receiverId = dm.getReceiverId();
        this.receiver = dm.getReceiver();
    }
}
