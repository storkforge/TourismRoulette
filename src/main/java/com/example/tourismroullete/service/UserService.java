package com.example.tourismroullete.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.tourismroullete.entities.UserEnt;
import com.example.tourismroullete.repository.UserRepo;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserEnt registerNewUser(UserEnt user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role if not specified
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        return userRepository.save(user);

    }

    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}


