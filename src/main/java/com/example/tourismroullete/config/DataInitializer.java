package com.example.tourismroullete.config;

import com.example.tourismroullete.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryService categoryService;

    @Autowired
    public DataInitializer(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void run(String... args) {
        // Initialize default categories
        categoryService.initializeDefaultCategories();
    }
}
