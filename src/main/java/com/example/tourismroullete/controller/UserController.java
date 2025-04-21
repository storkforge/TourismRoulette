package com.example.tourismroullete.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;

import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Visa profil-sidan
    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        String username = principal.getName(); // hämtar "DeenoDev00"
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // din egna entitet

        if (user == null) {
            return "redirect:/login";
        }

        if (user.getProfilePicture() != null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getProfilePicture());
            model.addAttribute("profilePictureBase64", base64Image);
        }


        model.addAttribute("user", user);
        return "profile";
    }

    // Uppdatera användarens info
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String username,
                                @RequestParam String email,
                                @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
                                Principal principal,
                                Model model) throws IOException {

        // Hämta inloggad användare
        String loggedInUsername = principal.getName();
        User user = userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Uppdatera användardata
        user.setUsername(username);
        user.setEmail(email);

        // Om ny profilbild finns, sätt den
        if (profilePicture != null && !profilePicture.isEmpty()) {
            user.setProfilePicture(profilePicture.getBytes());
        }

        // Spara användaren i databasen
        userRepository.save(user);

        // Uppdatera modellen med den uppdaterade användardatan
        model.addAttribute("user", user);

        // Om du har profilbild, kodar vi den till Base64 så att den kan visas på sidan
        if (user.getProfilePicture() != null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getProfilePicture());
            model.addAttribute("profilePictureBase64", base64Image);
        }

        // Återvänd till samma sida, utan redirect
        return "profile";  // Här returneras samma vy med uppdaterad användardata
    }


}

