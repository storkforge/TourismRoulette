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
                                Principal principal) throws IOException {

        String loggedInUsername = principal.getName();
        User user = userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setUsername(username);
        user.setEmail(email);

        if (profilePicture != null && !profilePicture.isEmpty()) {
            user.setProfilePicture(profilePicture.getBytes());
        }

        userRepository.save(user); // 🔥 Detta är det som faktiskt sparar till DB!

        return "redirect:/profile";
    }

}

