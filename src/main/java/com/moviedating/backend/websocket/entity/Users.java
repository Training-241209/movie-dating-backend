package com.moviedating.backend.websocket.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Users {
    @Id
    private String name;
    private String status;

}
