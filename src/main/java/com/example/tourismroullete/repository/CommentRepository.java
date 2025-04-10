package com.example.tourismroullete.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.tourismroullete.model.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    static Page<Comment> findByPostId(Long postId, Pageable pageable) {
        return null;


    }
}
