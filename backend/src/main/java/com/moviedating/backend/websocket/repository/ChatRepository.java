package com.moviedating.backend.websocket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moviedating.backend.websocket.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByChatId(String chatId);

    @Query("SELECT c FROM Chat c WHERE (c.senderId = :senderId AND c.recipientId = :recipientId) " +
       "OR (c.senderId = :recipientId AND c.recipientId = :senderId) " +
       "ORDER BY c.timestamp ASC")
List<Chat> findAllMessagesById(@Param("senderId") String senderId, @Param("recipientId") String recipientId);

}
