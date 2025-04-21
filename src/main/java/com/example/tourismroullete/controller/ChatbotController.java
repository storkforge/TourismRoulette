package com.example.tourismroullete.controller;

import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ai/RouletteBot")
public class ChatbotController {

    private final MistralAiChatModel chatModel;

    public ChatbotController(MistralAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostMapping
    public String chatWithAi(@RequestBody String prompt, HttpSession session) {
        // Call the AI model
        String response = chatModel.call(prompt);

        // Retrieve the current session history or initialize an empty list
        List<String> history = (List<String>) session.getAttribute("aiHistory");

        // Check if the retrieved object is a List<String> before casting
        if (history == null || !(history instanceof List)) {
            history = new ArrayList<>();
        }

        // Add the prompt and response to the session history
        history.add("User: " + prompt);
        history.add("AI: " + response);

        // Save the updated history back to the session
        session.setAttribute("aiHistory", history);

        // Return the AI response
        return response;
    }


    @GetMapping("/history")
    public List<String> getHistory(HttpSession session) {
        // Retrieve the history from the session (if exists)
        List<String> history = (List<String>) session.getAttribute("aiHistory");
        return history != null ? history : new ArrayList<>();
    }
}
