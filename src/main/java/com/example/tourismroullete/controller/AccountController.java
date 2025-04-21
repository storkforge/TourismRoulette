package com.example.tourismroullete.controller;

import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.tourismroullete.entities.User;





@Controller
@RequestMapping("/account")
public class AccountController {

    @GetMapping("/settings")
    public String accountSettings(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        return "profile"; // Det här ska matcha profile.html
    }
}

