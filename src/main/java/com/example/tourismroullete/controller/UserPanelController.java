package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Base64;

@Controller
public class UserPanelController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user-panel")
    public String UI(Model model, Principal principal) {
        // Hämta den inloggade användarens användarnamn
        String loggedInUsername = principal.getName();

        // Hämta användaren från databasen
        User user = userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Om användaren har en profilbild, konvertera den till base64 och skicka till Thymeleaf
        if (user.getProfilePicture() != null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getProfilePicture());
            model.addAttribute("profilePictureBase64", base64Image);
        }

        // Skicka användaren till Thymeleaf för att kunna visa andra användardata också
        model.addAttribute("user", user);

        // Skicka användardatan till Thymeleaf och ladda upp user-panel-sidan
        return "user-panel";
    }
}
