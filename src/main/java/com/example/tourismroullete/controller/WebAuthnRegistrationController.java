package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.WebauthnCredentials;
import com.example.tourismroullete.service.WebAuthnRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/webauthn")
@CrossOrigin(origins = "*") // Add if needed for cross-origin requests
@RequiredArgsConstructor
public class WebAuthnRegistrationController {

    private final WebAuthnRegistrationService registrationService;

    @PostMapping(
            value = "/register",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> registerCredential(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String credentialId,
            @RequestParam(required = false) String publicKeyCose,
            @RequestParam(required = false) Long signCount,
            @RequestBody(required = false) RegistrationRequest body
    ) {
        log.info("=== Received WebAuthn Registration Request ===");

        try {
            // Get parameters either from request params or body
            String finalUsername = username != null ? username : (body != null ? body.getUsername() : null);
            String finalCredentialId = credentialId != null ? credentialId : (body != null ? body.getCredentialId() : null);
            String finalPublicKeyCose = publicKeyCose != null ? publicKeyCose : (body != null ? body.getPublicKeyCose() : null);
            Long finalSignCount = signCount != null ? signCount : (body != null ? body.getSignCount() : null);

            log.info("Processing registration for user: {}", finalUsername);
            log.debug("CredentialId: {}, SignCount: {}", finalCredentialId, finalSignCount);

            // Parameter validation
            if (finalUsername == null || finalUsername.trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\": \"Username is required\"}");
            }

            WebauthnCredentials savedCredential = registrationService.registerCredentialForUser(
                    finalUsername, finalCredentialId, finalPublicKeyCose, finalSignCount);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(savedCredential);

        } catch (Exception e) {
            log.error("Registration failed", e);
            return ResponseEntity
                    .internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping(
            value = "/register/test",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> testRegistrationEndpoint() {
        log.info("Test endpoint reached");
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"status\": \"success\", \"message\": \"Registration endpoint is accessible\"}");
    }

    // Request body class with proper JSON annotations
    public static class RegistrationRequest {
        private String username;
        private String credentialId;
        private String publicKeyCose;
        private Long signCount;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getCredentialId() { return credentialId; }
        public void setCredentialId(String credentialId) { this.credentialId = credentialId; }
        public String getPublicKeyCose() { return publicKeyCose; }
        public void setPublicKeyCose(String publicKeyCose) { this.publicKeyCose = publicKeyCose; }
        public Long getSignCount() { return signCount; }
        public void setSignCount(Long signCount) { this.signCount = signCount; }
    }
}