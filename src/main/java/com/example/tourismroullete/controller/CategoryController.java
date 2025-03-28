package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.Category;
import com.example.tourismroullete.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Web UI endpoints

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        // Changed from "categories/list" to "categories" to match your template location
        return "categories";
    }

    @GetMapping("/categories/{id}")
    public String viewCategory(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        model.addAttribute("activities", categoryService.getCategoryById(id).getActivities());
        // You'll need to create this template or adjust the return value
        return "category-view";
    }

    @GetMapping("/admin/categories")
    public String adminCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        // You'll need to create this template or adjust the return value
        return "admin-categories";
    }

    @GetMapping("/admin/categories/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        // You'll need to create this template or adjust the return value
        return "category-form";
    }

    @GetMapping("/admin/categories/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        // You'll need to create this template or adjust the return value
        return "category-form";
    }

    // REST API endpoints

    @GetMapping("/api/categories")
    @ResponseBody
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/api/categories/{id}")
    @ResponseBody
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping("/api/admin/categories")
    @ResponseBody
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category newCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @PutMapping("/api/admin/categories/{id}")
    @ResponseBody
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/api/admin/categories/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
