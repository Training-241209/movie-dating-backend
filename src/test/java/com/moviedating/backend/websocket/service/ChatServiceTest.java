package com.moviedating.backend.websocket.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moviedating.backend.websocket.entity.Chat;
import com.moviedating.backend.websocket.repository.ChatRepository;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {
    @Mock
    private ChatRepository chatRepo;

    @Mock
    private ChatRoomService chatRoomService;

    @InjectMocks
    private ChatService chatService;

    @Test
    void testSaveMessage_Success(){
        Chat message = new Chat();
        message.setSenderId("user1");
        message.setRecipientId("user2");

        Optional<String> chatRoomId = Optional.of("chatRoom123");
        when(chatRoomService.getChatRoomId("user1", "user2", true)).thenReturn(chatRoomId);
        when(chatRepo.save(message)).thenReturn(message);

        Chat result = chatService.saveMessage(message);

        assertNotNull(result);
        assertEquals("chatRoom123", result.getChatId());
        verify(chatRoomService, times(1)).getChatRoomId("user1", "user2", true);
        verify(chatRepo, times(1)).save(message);
    }
    @Test
    void testSaveMessage_ChatRoomNotFound(){
        Chat message = new Chat();
        message.setSenderId("user1");
        message.setRecipientId("user2");

        when(chatRoomService.getChatRoomId("user1", "user2", true)).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class,
             () -> chatService.saveMessage(message));
        assertEquals("Chat Room Not Found", e.getMessage());
        verify(chatRoomService, times(1)).getChatRoomId("user1", "user2", true);
        verify(chatRepo, never()).save(any());
    }

    @Test
    void testFindChat_Success(){
        String senderId = "user1";
        String recipientId = "user2";

        Optional<String> chatRoomId = Optional.of("chatRoom123");
        List<Chat> mockChats = List.of(new Chat(), new Chat());

        when(chatRoomService.getChatRoomId(senderId, recipientId, false)).thenReturn(chatRoomId);
        when(chatRepo.findAllMessagesById(senderId, recipientId)).thenReturn(mockChats);

        List<Chat> result = chatService.findChats(senderId, recipientId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(chatRoomService, times(1)).getChatRoomId(senderId, recipientId, false);
        verify(chatRepo, times(1)).findAllMessagesById(senderId, recipientId);

    }

    @Test
    void testFindChats_ChatRoomNotFound(){

        String senderId = "user1";
        String recipientId = "user2";
  
        when(chatRoomService.getChatRoomId(senderId, recipientId, false)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> chatService.findChats(senderId, recipientId));
        assertEquals("Chat Room Not Found", exception.getMessage());
        verify(chatRoomService, times(1)).getChatRoomId(senderId, recipientId, false);
        verify(chatRepo, never()).findAllMessagesById(anyString(), anyString());
    }

}
