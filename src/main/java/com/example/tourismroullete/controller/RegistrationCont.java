package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.UserEnt;
import com.example.tourismroullete.DTOs.RegDTO;
import com.example.tourismroullete.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationCont {

    private final UserService userService;


    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new RegDTO());
        return "register";
    }

    @PostMapping
    public String registerUser(@Valid @ModelAttribute("registrationDto") RegDTO registrationDto,
                               BindingResult result, Model model) {

        // Check if passwords match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.registrationDto", "Passwords do not match");
        }
        // Check if username is available
        if (!userService.isUsernameAvailable(registrationDto.getUsername())) {
            result.rejectValue("username", "error.registrationDto", "Username is already taken");
        }
        // Check if email is available
        if (!userService.isEmailAvailable(registrationDto.getEmail())) {
            result.rejectValue("email", "error.registrationDto", "Email is already registered");
        }
        if (result.hasErrors()) {
            return "register";
        }
        UserEnt user = new UserEnt();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(registrationDto.getPassword());
        user.setEmail(registrationDto.getEmail());
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());

        try {
            userService.registerNewUser(user);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }

    }


}
