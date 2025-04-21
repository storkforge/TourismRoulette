package com.example.tourismroullete.repositories;

import com.example.tourismroullete.entities.PasskeyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasskeyuserRepository extends JpaRepository<PasskeyUser, Long> {
    Optional<PasskeyUser> findbyUser(String username);
    Optional<PasskeyUser> findByExternalId(String externalId);
}
