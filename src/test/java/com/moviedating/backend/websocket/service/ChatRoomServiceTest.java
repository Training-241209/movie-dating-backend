package com.moviedating.backend.websocket.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moviedating.backend.websocket.entity.ChatRoom;
import com.moviedating.backend.websocket.repository.ChatRoomRepository;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {
    
    @Mock
    private ChatRoomRepository chatRoomRepo;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Test
    void testGetChatRoomId_ChatRoomExists() {
        String senderId = "user1";
        String recipientId = "user2";
        String chatRoomId = "user1_user2";

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(chatRoomId);
        chatRoom.setSenderId(senderId);
        chatRoom.setRecipientId(recipientId);

        when(chatRoomRepo.findBySenderIdAndRecipientId(senderId, recipientId)).thenReturn(Optional.of(chatRoom));

        Optional<String> result = chatRoomService.getChatRoomId(senderId, recipientId, false);

        assertTrue(result.isPresent());
        assertEquals(chatRoomId, result.get());
        verify(chatRoomRepo, times(1)).findBySenderIdAndRecipientId(senderId, recipientId);
    }

}
