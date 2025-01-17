package com.moviedating.backend.websocket.controller;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.moviedating.backend.websocket.entity.Chat;
import com.moviedating.backend.websocket.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@WebMvcTest(ChatController.class)
public class ChatControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    private Chat message1;
    private Chat message2;

    @BeforeEach
    void setUp() {
        message1 = new Chat(1L, "chat1", "user1", "user2", "Hello!", new Date());
        message2 = new Chat(2L, "chat2", "user1", "user2", "How are you?", new Date());
    }

    @Test
    void testFindChats_NoMessages() throws Exception {
        when(chatService.findChats("user1", "user2")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/messages/user1/user2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        verify(chatService, times(1)).findChats("user1", "user2");
    }
}
