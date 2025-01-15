package com.moviedating.backend.websocket.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.moviedating.backend.websocket.entity.Users;
import com.moviedating.backend.websocket.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @MessageMapping("/addUser")
    @SendTo("/user/topic")
    public Users addUser(@Payload Users user) {
        userService.saveUser(user);
        return user;
    }

    @MessageMapping("/disconnectUser")
    @SendTo("/user/topic")
    public Users disconnect(@Payload Users user) {
        userService.disconnect(user);
        return user;
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<Users>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }
}
