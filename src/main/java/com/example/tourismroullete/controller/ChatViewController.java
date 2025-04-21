package com.example.tourismroullete.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatViewController {

    @GetMapping("/chat")
    public String showChatPage(Model model) {
        // Optionally, pass data to the model for initial rendering
        return "chatbot";  // This corresponds to the "chatbot.html" template
    }
}
