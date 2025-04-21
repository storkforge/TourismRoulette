package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.ChatMessage;
import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.repositories.ChatMessageRepository;
import com.example.tourismroullete.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ai/RouletteBot")
public class ChatbotController {

    private final MistralAiChatModel chatModel;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatbotController(MistralAiChatModel chatModel,
                             ChatMessageRepository chatMessageRepository,
                             UserRepository userRepository) {
        this.chatModel = chatModel;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public String chatWithAi(@RequestBody String prompt, HttpSession session, Principal principal) {
        String response = chatModel.call(prompt);

        // Handle authenticated user
        if (principal != null) {
            Optional<User> userOpt = userRepository.findByUsername(principal.getName());

            userOpt.ifPresent(user -> {
                chatMessageRepository.save(new ChatMessage(null, user, "USER", prompt, LocalDateTime.now()));
                chatMessageRepository.save(new ChatMessage(null, user, "AI", response, LocalDateTime.now()));
            });

        } else {
            // Fallback to session-based history
            Optional<List<String>> historyOpt = Optional.ofNullable((List<String>) session.getAttribute("aiHistory"));

            List<String> history = historyOpt.orElse(new ArrayList<>());
            history.add("User: " + prompt);
            history.add("AI: " + response);

            session.setAttribute("aiHistory", history);
        }

        return response;
    }

    @GetMapping("/history")
    public List<String> getHistory(HttpSession session, Principal principal) {
        List<String> history = new ArrayList<>();

        // Handle authenticated user
        if (principal != null) {
            Optional<User> userOpt = userRepository.findByUsername(principal.getName());

            userOpt.ifPresent(user -> {
                chatMessageRepository.findByUserOrderByTimestampAsc(user)
                        .forEach(msg -> history.add(msg.getRole() + ": " + msg.getMessage()));
            });

        } else {
            // Handle session-based history
            Optional<List<String>> sessionHistoryOpt = Optional.ofNullable((List<String>) session.getAttribute("aiHistory"));
            sessionHistoryOpt.ifPresent(history::addAll);
        }

        return history;
    }
}
