package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.ChatMessage;
import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.repositories.ChatMessageRepository;
import com.example.tourismroullete.repositories.UserRepository;
import lombok.Data;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai/RouletteBot")
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatClient chatClient;

    @Autowired
    public ChatController(ChatMessageRepository chatMessageRepository,
                          UserRepository userRepository,
                          ChatClient chatClient) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.chatClient = chatClient;
    }

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody ChatRequest request, Principal principal) {
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        User user = userOpt.get();
        String sessionId = request.getSessionId();
        String message = request.getMessage();

        // Save user message
        ChatMessage userMsg = new ChatMessage();
        userMsg.setUser(user);
        userMsg.setSender("User");
        userMsg.setText(message);
        userMsg.setTimestamp(LocalDateTime.now());
        userMsg.setSessionId(sessionId);
        chatMessageRepository.save(userMsg);

        // Get AI response
        String aiResponse = chatClient.prompt().user(message).call().content();

        // Save AI response
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setUser(user);
        aiMsg.setSender("AI");
        aiMsg.setText(aiResponse);
        aiMsg.setTimestamp(LocalDateTime.now());
        aiMsg.setSessionId(sessionId);
        chatMessageRepository.save(aiMsg);

        return ResponseEntity.ok(aiResponse);
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, List<ChatMessageDTO>>> getChatHistory(Principal principal) {
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userOpt.get();
        List<ChatMessage> messages = chatMessageRepository.findByUserOrderByTimestampAsc(user);

        Map<String, List<ChatMessageDTO>> historyGroupedBySession = messages.stream()
                .collect(Collectors.groupingBy(
                        ChatMessage::getSessionId,
                        LinkedHashMap::new,
                        Collectors.mapping(ChatMessageDTO::new, Collectors.toList())
                ));

        return ResponseEntity.ok(historyGroupedBySession);
    }

    // DTO for chat requests
    @Data
    static class ChatRequest {
        private String message;
        private String sessionId;
    }

    // DTO for chat responses
    @Data
    static class ChatMessageDTO {
        private String sender;
        private String text;
        private LocalDateTime timestamp;

        public ChatMessageDTO(ChatMessage msg) {
            this.sender = msg.getSender();
            this.text = msg.getText();
            this.timestamp = msg.getTimestamp();
        }
    }
}
