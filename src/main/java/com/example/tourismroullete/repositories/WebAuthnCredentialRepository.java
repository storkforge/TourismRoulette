package com.example.tourismroullete.repositories;

import com.example.tourismroullete.entities.WebauthnCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebAuthnCredentialRepository extends JpaRepository<WebauthnCredentials, Long> {
    // Credential methods
    Optional<WebauthnCredentials> findByCredentialId(String credentialId);

    List<WebauthnCredentials> findByUserId(Long userId);

    List<WebauthnCredentials> findByPasskeyUser_Id(Long userId);

    // PasskeyUser methods
    Optional<WebauthnCredentials> findByPasskeyUser_Name(String username);

    Optional<WebauthnCredentials> findByPasskeyUser_ExternalId(String externalId);

    boolean existsByPasskeyUser_Name(String username);

    boolean existsByPasskeyUser_ExternalId(String externalId);
}