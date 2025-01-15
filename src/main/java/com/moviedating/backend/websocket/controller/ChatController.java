package com.moviedating.backend.websocket.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.moviedating.backend.websocket.entity.Chat;
import com.moviedating.backend.websocket.entity.ChatNotification;
import com.moviedating.backend.websocket.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<Chat>> findChats(@PathVariable String senderId, @PathVariable String recipientId) {
        return ResponseEntity.ok(chatService.findChats(senderId, recipientId));
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload Chat message) {
        Chat savedMessage = chatService.saveMessage(message);
        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(), "/queue/messages", new ChatNotification(
                        savedMessage.getId(),
                        savedMessage.getSenderId(),
                        savedMessage.getRecipientId(),
                        savedMessage.getContent()));
    }
}
