package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.model.Post;
import com.example.tourismroullete.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostWebController {

    private final PostService postService;

    // Visa alla inlägg på huvudsidan
    @GetMapping("/posts")
    public String getAllPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "posts"; // Thymeleaf kommer att leta efter posts.html
    }


    // Visa en form för att skapa nytt inlägg
    @GetMapping("/posts/new")
    public String createPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "create-post"; // Thymeleaf kommer att leta efter create-post.html
    }

    // Hantera skapandet av ett nytt inlägg
    @PostMapping("/posts")
    public String createPost(@ModelAttribute Post post,
                             @RequestParam("author") User author,
                             @RequestParam(value = "image", required = false) MultipartFile image) {
        postService.createPost(post, author, image);
        return "redirect:/posts"; // Här ska det inte vara någon paginering som stör
    }

    @GetMapping("/my-posts")
    public String getMyPosts(Model model, Principal principal) {
        String username = principal.getName();
        List<Post> myPosts = postService.getPostsByUsername(username);
        model.addAttribute("myPosts", myPosts);
        return "my-posts"; // vi skapar my-posts.html nedan
    }


    @GetMapping("/posts/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id).orElseThrow();
        model.addAttribute("post", post);
        return "edit-posts"; // Skapa denna HTML-sida
    }

    @PostMapping("/posts/edit/{id}")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute("post") Post updatedPost,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             Principal principal) {
        String username = principal.getName();

        // Sätt bilddata manuellt om fil är uppladdad
        if (image != null && !image.isEmpty()) {
            try {
                updatedPost.setImage(image.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        postService.updatePost(id, updatedPost, username); // Skicka vidare till service
        return "redirect:/my-posts";
    }




    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id, Principal principal) {
        String username = principal.getName();
        User user = postService.getPostsByUsername(username).get(0).getAuthor(); // exempel
        postService.deletePost(id, user);
        return "redirect:/my-posts";
    }


}
