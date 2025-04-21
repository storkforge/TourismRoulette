package com.example.tourismroullete.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String sender;  // "User" or "AI"

    @Column(length = 5000) // Increase the length here
    private String text;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "session_id", nullable = false)
    private String sessionId;


    // Add constructors, getters, and setters if needed
}
