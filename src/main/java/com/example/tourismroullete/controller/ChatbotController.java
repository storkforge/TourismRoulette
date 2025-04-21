package com.example.tourismroullete.controller;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mistralai.MistralAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;

import java.util.*;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    private final MistralAiChatClient chatClient;
    private final Map<String, List<Message>> sessions = new HashMap<>();

    @Autowired
    public ChatbotController(MistralAiChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/{sessionId}")
    public Map<String, String> chat(@PathVariable String sessionId, @RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        if (userMessage == null || userMessage.isBlank()) {
            return Map.of("error", "Message is required");
        }

        sessions.putIfAbsent(sessionId, new ArrayList<>());
        List<Message> history = sessions.get(sessionId);

        // Add user's message to history
        history.add(new UserMessage(userMessage));


        Prompt prompt = new Prompt(history);
        var response = chatClient.call(prompt).getResult().getOutput().getContent();

        // Add AI response to history
        history.add(new AssistantMessage(response));

        return Map.of("response", response);
    }
}
