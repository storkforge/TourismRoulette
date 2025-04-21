package com.example.tourismroullete.controller;

import com.example.tourismroullete.model.Comment;
import com.example.tourismroullete.model.Post;
import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // Hämta alla inlägg via REST API
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // Hämta inlägg med id via REST API
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Controller
    public class PostViewController {

        @GetMapping("/create-post")
        public String createPostPage() {
            return "create-post"; // Namnet på din HTML-fil utan .html
        }
    }

    // Skapa ett nytt inlägg via REST API
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Post> createPost(
            @RequestParam("title") String title,
            @RequestParam("location") String location,
            @RequestParam("content") String content,
            @RequestParam(value = "isPublic", defaultValue = "true") boolean isPublic,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal User user
    ) {
        String userName = principal.getName();
        User user = userService.findByUsername(userName).orElseThrow();

        Post post = new Post();
        post.setAuthor(user);
        post.setTitle(title);
        post.setLocation(location);
        post.setContent(content);
        post.setPublic(isPublic);

        return ResponseEntity.ok(postService.createPost(post, user, imageFile));
    }

    // Uppdatera ett inlägg via REST API
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id,
                                           @RequestBody Post updatedPost,
                                           @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(postService.updatePost(id, updatedPost, user));
    }

    // Ta bort ett inlägg via REST API
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @AuthenticationPrincipal User user) {
        postService.deletePost(id, user);
        return ResponseEntity.noContent().build();
    }

    // Lägg till en kommentar till ett inlägg via REST API
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Long postId,
                                              @RequestBody String content,
                                              @AuthenticationPrincipal User user) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        Comment comment = postService.addComment(post, content, user);
        return ResponseEntity.ok(comment);
    }

    // Gilla eller ogilla ett inlägg via REST API
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Void> toggleLike(@PathVariable Long postId,
                                           @AuthenticationPrincipal User user) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        postService.toggleLike(post, user);
        return ResponseEntity.ok().build();
    }



}

