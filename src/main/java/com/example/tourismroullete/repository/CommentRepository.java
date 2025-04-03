package com.example.tourismroullete.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.tourismroullete.model.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);
}
