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
import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.exception.ResourceNotFoundException;
import com.example.tourismroullete.exception.UnauthorizedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
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

    public Post createPost(Post post, User user, MultipartFile imageFile) {
        post.setAuthor(user);

        if (imageFile != null && !imageFile.isEmpty()) {
            try (InputStream inputStream = imageFile.getInputStream();
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] temp = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(temp)) != -1) {
                    buffer.write(temp, 0, bytesRead);
                }

                post.setImage(buffer.toByteArray()); // ← tydligare och ibland stabilare
            } catch (IOException e) {
                throw new RuntimeException("Kunde inte läsa bildfilen", e);
            }
        }

        return postRepository.save(post);
    }



        //post.setAuthor(user);
       // post.setCreatedAt(LocalDateTime.now());

        // Spara via repository
        //return postRepository.save(post);



    public Post updatePost(Long id, Post updatedPost, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getAuthor().getUsername().equals(username)) {
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

    public List<Post> getPostsByUsername(String username) {
        return postRepository.findByAuthor_UsernameOrderByCreatedAtDesc(username);
    }









}