package com.example.tourismroullete.service;

import com.example.tourismroullete.entities.Category;
import com.example.tourismroullete.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = getCategoryById(id);

        // Check if the new name already exists for another category
        if (!category.getName().equals(categoryDetails.getName()) &&
                categoryRepository.existsByName(categoryDetails.getName())) {
            throw new IllegalArgumentException("Category with name '" + categoryDetails.getName() + "' already exists");
        }

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setIconName(categoryDetails.getIconName());
        category.setNameKey(categoryDetails.getNameKey());

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);

        // Remove this category from all associated activities
        category.getActivities().forEach(activity -> activity.getCategories().remove(category));

        categoryRepository.delete(category);
    }

    @Transactional
    public void initializeDefaultCategories() {
        // Only create default categories if none exist
        if (categoryRepository.count() == 0) {
            createDefaultCategory("Adventure", "Exciting outdoor activities for thrill-seekers", "hiking");
            createDefaultCategory("Relaxation", "Peaceful activities for unwinding and rejuvenation", "spa");
            createDefaultCategory("Culture", "Activities focused on local traditions and heritage", "museum");
            createDefaultCategory("Food & Drink", "Culinary experiences and tastings", "restaurant");
            createDefaultCategory("Nature", "Outdoor activities in natural settings", "tree");
            createDefaultCategory("History", "Activities related to historical sites and events", "landmark");
            createDefaultCategory("Entertainment", "Fun activities for enjoyment and amusement", "theater");
            createDefaultCategory("Shopping", "Retail experiences and local markets", "shopping-bag");
        }
    }

    private void createDefaultCategory(String name, String description, String iconName) {
        if (!categoryRepository.existsByName(name)) {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            category.setIconName(iconName);
            category.setNameKey("category." + name.toLowerCase().replace(" & ", "_").replace(" ", "_"));
            categoryRepository.save(category);
        }
    }
}
