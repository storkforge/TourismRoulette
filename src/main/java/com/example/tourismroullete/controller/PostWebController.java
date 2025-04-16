package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.model.Post;
import com.example.tourismroullete.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
}
