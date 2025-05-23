package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.entities.Category;
import com.example.tourismroullete.entities.UserCategory;
import com.example.tourismroullete.repositories.CategoryRepository;
import com.example.tourismroullete.repositories.UserRepository;
import com.example.tourismroullete.repositories.UserCategoryRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PreferenceController {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserCategoryRepository userCategoryRepository;

    public PreferenceController(UserRepository userRepository, CategoryRepository categoryRepository, UserCategoryRepository userCategoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.userCategoryRepository = userCategoryRepository;
    }

    @GetMapping("/preferences")
    public String showPreferences(Authentication authentication, Model model) {
        String username = authentication.getName();
        List<Category> categories = categoryRepository.findAll();

        // Get the current user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get the user's existing preferences
        List<UserCategory> userCategories = userCategoryRepository.findByUserId(user.getId());

        // Extract just the category IDs to make checking easier in the template
        List<Long> userPreferenceIds = userCategories.stream()
                .map(uc -> uc.getCategory().getId())
                .collect(Collectors.toList());

        model.addAttribute("categories", categories);
        model.addAttribute("username", username);
        model.addAttribute("userPreferences", userPreferenceIds);

        return "preferences";
    }

    @PostMapping("/preferences")
    @Transactional
    public String savePreferences(@RequestParam String username, @RequestParam(required = false) List<Long> preferences) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        // Clear previous preferences for this user
        userCategoryRepository.deleteByUserId(user.getId());

        // Save new preferences if any were selected
        if (preferences != null && !preferences.isEmpty()) {
            for (Long categoryId : preferences) {
                Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
                UserCategory userCategory = new UserCategory();
                userCategory.setUser(user);
                userCategory.setCategory(category);
                userCategoryRepository.save(userCategory);
            }
        }

        return "redirect:/user-panel";  // Redirect to user panel after saving preferences
    }
}
