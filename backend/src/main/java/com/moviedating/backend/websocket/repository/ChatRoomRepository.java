package com.moviedating.backend.websocket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moviedating.backend.websocket.entity.ChatRoom;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, String>{
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);

    List<ChatRoom> findBySenderId(String senderId);
}
