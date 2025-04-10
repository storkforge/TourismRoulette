package com.example.tourismroullete.service;

import com.example.tourismroullete.model.Like;
import com.example.tourismroullete.model.Post;
import com.example.tourismroullete.repository.LikeRepository;
import com.example.tourismroullete.repository.PostRepository;
import com.example.tourismroullete.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import com.example.tourismroullete.model.Comment;
import com.example.tourismroullete.model.User;
import com.example.tourismroullete.exception.ResourceNotFoundException;
import com.example.tourismroullete.exception.UnauthorizedException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository; // 🆕

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Post createPost(Post post, User author) {
        post.setAuthor(author);
        return postRepository.save(post);
    }

    public Post updatePost(Long id, Post updatedPost, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getAuthor().equals(user)) {
            throw new UnauthorizedException("Not authorized to edit this post");
        }

        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setLocation(updatedPost.getLocation());
        post.setPublic(updatedPost.isPublic());

        return postRepository.save(post);
    }

    public void deletePost(Long id, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getAuthor().equals(user)) {
            throw new UnauthorizedException("Not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    public Page<Post> getFeed(Pageable pageable) {
        return postRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
    }

    public Comment addComment(Post post, String content, User author) {
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(content);
        comment.setAuthor(author);
        return commentRepository.save(comment);
    }

    public void toggleLike(Post post, User user) {
        Optional<Like> existingLike = post.getLikes().stream()
                .filter(like -> like.getUser().equals(user))
                .findFirst();

        if (existingLike.isPresent()) {
            post.getLikes().remove(existingLike.get());
            likeRepository.delete(existingLike.get());
        } else {
            Like newLike = new Like();
            newLike.setUser(user);
            newLike.setPost(post);
            post.getLikes().add(newLike);
            likeRepository.save(newLike);
        }

        postRepository.save(post);
    }
}