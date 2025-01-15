package com.moviedating.backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviedating.backend.Entity.Account;
import com.moviedating.backend.Service.AccountService;
import com.moviedating.backend.dtos.AccountDTO;
import com.moviedating.backend.websocket.entity.Chat;
import com.moviedating.backend.websocket.service.ChatService;

import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ChatService chatService;

    @GetMapping("/{username}")
    public List<AccountDTO> getMatches(@PathVariable String username) {
        return accountService.findMatches(username);
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<Chat>> getMessagesBetweenUsers(@PathVariable String senderId, @PathVariable String recipientId) {
        List<Chat> chats = chatService.findChats(senderId, recipientId);
        return ResponseEntity.ok(chats);
    }
}
