package com.moviedating.backend.websocket.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.moviedating.backend.websocket.entity.Users;
import com.moviedating.backend.websocket.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    public void saveUser(Users user){
        user.setStatus("ONLINE");
        userRepo.save(user);
    }

    public void disconnect(Users user){
        Optional<Users> optUser = userRepo.findById(user.getName());
        if(optUser.isPresent()){
            Users userStore = optUser.get();
            userStore.setStatus("OFFLINE");
            userRepo.save(userStore);
        }
    }

    public List<Users> findConnectedUsers(){
        return userRepo.findAllByStatus("ONLINE");
    }
}
