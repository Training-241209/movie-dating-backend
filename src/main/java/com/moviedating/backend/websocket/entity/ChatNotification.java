package com.moviedating.backend.websocket.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatNotification {
    private Long id;
    private String senderId;
    private String recipientId;
    private String content;
}
