package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.ChatMessage;
import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.repositories.ChatMessageRepository;
import com.example.tourismroullete.repositories.UserRepository;
import com.example.tourismroullete.service.ChatCacheService;
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
    private final ChatCacheService chatCacheService;

    @Autowired
    public ChatController(ChatMessageRepository chatMessageRepository,
                          UserRepository userRepository,
                          ChatClient chatClient,
                          ChatCacheService chatCacheService) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.chatClient = chatClient;
        this.chatCacheService = chatCacheService;
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

        // 1. Try to retrieve cached AI response
        String cachedResponse = chatCacheService.getCachedResponse(user.getId(), message);
        String aiResponse;

        if (cachedResponse != null) {
            aiResponse = cachedResponse;
        } else {
            // 2. Not cached → fetch from AI model
            aiResponse = chatClient.prompt().user(message).call().content();

            // 3. Cache the response
            chatCacheService.cacheResponse(user.getId(), message, aiResponse);
        }

        // 4. Save user and AI messages to DB
        saveChatMessage(user, "User", message, sessionId);
        saveChatMessage(user, "AI", aiResponse, sessionId);

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

    private void saveChatMessage(User user, String sender, String text, String sessionId) {
        ChatMessage msg = new ChatMessage();
        msg.setUser(user);
        msg.setSender(sender);
        msg.setText(text);
        msg.setTimestamp(LocalDateTime.now());
        msg.setSessionId(sessionId);
        chatMessageRepository.save(msg);
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
