package com.example.tourismroullete.repositories;

import com.example.tourismroullete.entities.ChatMessage;
import com.example.tourismroullete.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserOrderByTimestampAsc(User user);
}
