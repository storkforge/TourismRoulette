package com.example.tourismroullete.repositories;

import com.example.tourismroullete.entities.ChatMessage;
import com.example.tourismroullete.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserOrderByTimestampAsc(User user);
    List<ChatMessage> findByUserAndSessionIdOrderByTimestampAsc(User user, String sessionId);

    @Query("SELECT DISTINCT c.sessionId FROM ChatMessage c WHERE c.user = :user ORDER BY MAX(c.timestamp) DESC")
    List<String> findSessionIdsByUser(@Param("user") User user);


    void deleteByUser(User user);

}
