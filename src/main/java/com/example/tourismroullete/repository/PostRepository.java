package com.example.tourismroullete.repository;


import com.example.tourismroullete.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findByAuthorIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Post> findByAuthor_UsernameOrderByCreatedAtDesc(String username);

}
