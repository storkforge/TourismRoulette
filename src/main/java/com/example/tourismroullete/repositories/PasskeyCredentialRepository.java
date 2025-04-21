package com.example.tourismroullete.repositories;

import com.example.tourismroullete.entities.WebauthnCredentials;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PasskeyCredentialRepository extends CrudRepository<WebauthnCredentials, Long> {
    Optional<WebauthnCredentials> findByCredentialId(String credentialId);

    @Query("SELECT c FROM WebauthnCredentials c WHERE c.passkeyUser.id = :passkeyUserId")
    List<WebauthnCredentials> findByPasskeyUserId(@Param("passkeyUserId") Long passkeyUserId);
}
