package com.example.tourismroullete.repository;

import com.example.tourismroullete.model.Like;
import com.example.tourismroullete.model.Post;
import com.example.tourismroullete.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// LikeRepository.java - Fixa detta!
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByPostAndUser(Post post, User user);
    void deleteByPostAndUser(Post post, User user);
}