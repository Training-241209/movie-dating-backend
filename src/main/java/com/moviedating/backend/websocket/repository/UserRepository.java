package com.moviedating.backend.websocket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moviedating.backend.websocket.entity.Users;

public interface UserRepository extends JpaRepository<Users, String> {
    List<Users> findAllByStatus(String status);
}
