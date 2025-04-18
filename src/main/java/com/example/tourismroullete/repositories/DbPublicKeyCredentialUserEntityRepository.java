package com.example.tourismroullete.repositories;

import com.example.tourismroullete.entities.PasskeyUser;
import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.entities.WebauthnCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.webauthn.api.Bytes;
import org.springframework.security.web.webauthn.api.ImmutablePublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Base64;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DbPublicKeyCredentialUserEntityRepository implements PublicKeyCredentialUserEntityRepository {

    private final WebAuthnCredentialRepository webAuthnCredentialRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PublicKeyCredentialUserEntity findById(Bytes id) {
        String externalId = Base64.getUrlEncoder().withoutPadding().encodeToString(id.getBytes());
        log.info("findById: id={}", externalId);
        return webAuthnCredentialRepository.findByPasskeyUser_ExternalId(externalId)
                .map(this::mapToUserEntity)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public PublicKeyCredentialUserEntity findByUsername(String username) {
        log.info("findByUsername: username={}", username);
        return webAuthnCredentialRepository.findByPasskeyUser_Name(username)
                .map(this::mapToUserEntity)
                .orElse(null);
    }

    @Override
    @Transactional
    public void save(PublicKeyCredentialUserEntity userEntity) {
        log.info("save: username={}, externalId={}", userEntity.getName(),
                Base64.getUrlEncoder().withoutPadding().encodeToString(userEntity.getId().getBytes()));

        String externalId = Base64.getUrlEncoder().withoutPadding().encodeToString(userEntity.getId().getBytes());

        // Get the currently authenticated user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        // Create or update WebauthnCredentials
        WebauthnCredentials credential = webAuthnCredentialRepository
                .findByPasskeyUser_ExternalId(externalId)
                .orElse(new WebauthnCredentials());

        // Set up the PasskeyUser if it doesn't exist
        if (credential.getPasskeyUser() == null) {
            PasskeyUser passkeyUser = new PasskeyUser();
            passkeyUser.setExternalId(externalId);
            passkeyUser.setName(userEntity.getName());
            passkeyUser.setDisplayName(userEntity.getDisplayName());
            credential.setPasskeyUser(passkeyUser);
        }

        // Store the actual WebAuthn credential data
        credential.setCredentialId(externalId);
        // Note: The actual publicKeyCose and signCount will be set by the UserCredentialRepository
        // during the registration process. We're just creating the initial user entity here.
        credential.setUser(currentUser);

        webAuthnCredentialRepository.save(credential);
    }

    @Override
    @Transactional
    public void delete(Bytes id) {
        String externalId = Base64.getUrlEncoder().withoutPadding().encodeToString(id.getBytes());
        log.info("delete: id={}", externalId);
        webAuthnCredentialRepository.findByPasskeyUser_ExternalId(externalId)
                .ifPresent(webAuthnCredentialRepository::delete);
    }

    private PublicKeyCredentialUserEntity mapToUserEntity(WebauthnCredentials credential) {
        PasskeyUser passkeyUser = credential.getPasskeyUser();
        return ImmutablePublicKeyCredentialUserEntity.builder()
                .id(Bytes.fromBase64(passkeyUser.getExternalId()))
                .name(passkeyUser.getName())
                .displayName(passkeyUser.getDisplayName())
                .build();
    }
}