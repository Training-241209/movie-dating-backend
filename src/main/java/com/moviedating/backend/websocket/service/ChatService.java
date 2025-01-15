package com.moviedating.backend.websocket.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.moviedating.backend.websocket.entity.Chat;
import com.moviedating.backend.websocket.repository.ChatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepo;
    private final ChatRoomService chatRoomService;

    public Chat saveMessage(Chat message) {
        Optional<String> chatId = chatRoomService.getChatRoomId(message.getSenderId(), message.getRecipientId(), true);
        if (chatId.isEmpty()) {
            throw new RuntimeException("Chat Room Not Found");
        }
        message.setChatId(chatId.get());
        return chatRepo.save(message);
    }

    public List<Chat> findChats(String senderId, String recipientId) {
        Optional<String> chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        if (chatId.isEmpty()) {
            throw new RuntimeException("Chat Room Not Found");
        }
        // return chatRepo.findByChatId(chatId.get());
        return chatRepo.findAllMessagesById(senderId, recipientId);
    }

}
