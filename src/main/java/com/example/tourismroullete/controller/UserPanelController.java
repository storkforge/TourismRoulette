package com.example.tourismroullete.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class UserPanelController {
    @GetMapping("/user-panel")
    public String UI() {
        return "/user-panel"; // Laddar src/main/resources/templates/index.html
    }
}
