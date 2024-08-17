package com.echo.echo.domain.dm.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "dm")
@NoArgsConstructor
public class Dm {

    @Id
    private String id;

//  private String nickname;
    private Long senderId;
    private String sender;
    private Long receiverId;
    private String receiver;

    public Dm(Long senderId, String sender, Long receiverId, String receiver) {
        this.senderId = senderId;
        this.sender = sender;
        this.receiverId = receiverId;
        this.receiver = receiver;
    }

}
