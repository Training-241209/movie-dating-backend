package com.moviedating.backend.Entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="conversation")
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer conversationId;

    @ManyToOne
    @JoinColumn(name = "participant1_id", nullable=false)
    private Account participant1;

    @ManyToOne
    @JoinColumn(name="participant2_id", nullable=false)
    private Account participant2;

    @OneToMany(mappedBy="conversation", cascade = CascadeType.ALL)
    private List<Message> messages;

    @Column
    private LocalDateTime timeCreated;


}
