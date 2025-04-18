
package com.example.tourismroullete.service;

import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.entities.WebauthnCredentials;
import com.example.tourismroullete.repositories.UserRepository;
import com.example.tourismroullete.repositories.WebAuthnCredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebAuthnRegistrationService {

    private final UserRepository userRepository;
    private final WebAuthnCredentialRepository credentialRepository;

    @Transactional
    public WebauthnCredentials registerCredentialForUser(
            String username,
            String credentialId,
            String publicKeyCose,
            Long signCount
    ) {
        log.info("Starting WebAuthn credential registration for user: {}", username);

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
            log.debug("Found user with ID: {}", user.getId());

            // Check for existing credential
            Optional<WebauthnCredentials> existingCredential = credentialRepository.findByCredentialId(credentialId);
            if (existingCredential.isPresent()) {
                log.warn("Credential ID already exists: {}", credentialId);
                throw new IllegalArgumentException("Credential already registered");
            }

            WebauthnCredentials credential = WebauthnCredentials.builder()
                    .credentialId(credentialId)
                    .publicKeyCose(publicKeyCose)
                    .signCount(signCount)
                    .user(user)
                    .build();

            log.debug("Attempting to save credential to database");
            WebauthnCredentials savedCredential = credentialRepository.save(credential);
            log.info("Successfully saved credential with ID: {} for user: {}", savedCredential.getId(), username);

            return savedCredential;
        } catch (DataAccessException e) {
            log.error("Database error during credential registration: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save credential", e);
        }
    }

    @Transactional(readOnly = true)
    public boolean hasRegisteredCredentials(String username) {
        log.debug("Checking for registered credentials for user: {}", username);
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
            return !credentialRepository.findByUserId(user.getId()).isEmpty();
        } catch (Exception e) {
            log.error("Error checking credentials for user: {}", username, e);
            return false;
        }
    }
}