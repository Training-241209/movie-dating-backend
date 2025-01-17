package com.moviedating.backend.websocket.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moviedating.backend.websocket.entity.Users;
import com.moviedating.backend.websocket.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserService userService;

    @Test
    void testSaveUser_Success() {
        Users user = new Users();
        user.setName("testuser123");

        userService.saveUser(user);

        assertEquals("ONLINE", user.getStatus()); 
        verify(userRepo, times(1)).save(user); 
    }
       @Test
    void testDisconnect_UserExists() {
        Users user = new Users();
        user.setName("testuser123");

        Users storedUser = new Users();
        storedUser.setName("testuser123");
        storedUser.setStatus("ONLINE");

        when(userRepo.findById("testuser123")).thenReturn(Optional.of(storedUser));

        userService.disconnect(user);

        assertEquals("OFFLINE", storedUser.getStatus()); 
        verify(userRepo, times(1)).findById("testuser123"); 
        verify(userRepo, times(1)).save(storedUser); 
    }

    @Test
    void testDisconnect_UserDoesNotExist() {
        
        Users user = new Users();
        user.setName("nonexistentuser");

        when(userRepo.findById("nonexistentuser")).thenReturn(Optional.empty());

        userService.disconnect(user);

        verify(userRepo, times(1)).findById("nonexistentuser"); 
        verify(userRepo, never()).save(any()); 
    }

    @Test
    void testFindConnectedUsers_Success() {
        Users user1 = new Users();
        user1.setName("user1");
        user1.setStatus("ONLINE");

        Users user2 = new Users();
        user2.setName("user2");
        user2.setStatus("ONLINE");

        List<Users> mockUsers = List.of(user1, user2);

        when(userRepo.findAllByStatus("ONLINE")).thenReturn(mockUsers);

        List<Users> result = userService.findConnectedUsers();

        assertNotNull(result);
        assertEquals(2, result.size()); 
        verify(userRepo, times(1)).findAllByStatus("ONLINE"); 
        }
}
