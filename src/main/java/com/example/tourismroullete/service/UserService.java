package com.example.tourismroullete.service;

import com.example.tourismroullete.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.tourismroullete.entities.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Method to encode the password
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // Method to check if username is available
    public boolean isUsernameAvailable(String username) {
        return userRepository.existsByUsername(username);
    }

    // Method to check if email is available
    public boolean isEmailAvailable(String email) {
        return userRepository.existsByEmail(email);
    }

    // Method to find a user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public User registerNewUser(User user) {
        // Encode the password before saving it
        String encodedPassword = encodePassword(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);  // Save the user to the DB and return it
    }
}
