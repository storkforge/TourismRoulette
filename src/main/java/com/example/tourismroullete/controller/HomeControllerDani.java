package com.example.tourismroullete.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HomeControllerDani {
    @GetMapping("/map")
    public String home() {
        return "/map"; // Laddar src/main/resources/templates/index.html
    }
}
