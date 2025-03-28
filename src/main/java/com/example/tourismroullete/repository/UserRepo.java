package com.example.tourismroullete.repository;

import com.example.tourismroullete.entities.UserEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEnt, Long> {
    Optional<UserEnt> findByUsername(String username);
    Optional<UserEnt> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);


}
