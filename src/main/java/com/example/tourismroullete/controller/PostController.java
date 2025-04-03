package com.example.tourismroullete.controller;


import com.example.tourismroullete.service.PostService;
import com.example.tourismroullete.model.Post;
import com.example.tourismroullete.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Post createPost(@RequestBody Post post, @AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser) {
        User user = new User();
        user.setUsername(securityUser.getUsername());
        return postService.createPost(post, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser) {
        User user = new User();
        user.setUsername(securityUser.getUsername());
        postService.deletePost(id, user);
        return ResponseEntity.noContent().build();
    }
}
