package com.moviedating.backend.websocket.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.moviedating.backend.websocket.entity.ChatRoom;
import com.moviedating.backend.websocket.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/chatRoom/{senderId}")
    public ResponseEntity<List<ChatRoom>> findChatRooms(@PathVariable String senderId){
        return ResponseEntity.ok(chatRoomService.getAllSenderChatRooms(senderId));
    }
}
