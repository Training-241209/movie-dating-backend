package com.moviedating.backend.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name="message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name="conversation_id", nullable=false)
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name="sender_id", nullable = false)
    private Account sender;

    @Column(nullable = false)
    private String messageBody;

    @Column
    private LocalDateTime timeStamp;


}
