
package com.example.tourismroullete.service;

import com.example.tourismroullete.entities.Activity;
import com.example.tourismroullete.entities.Category;
import com.example.tourismroullete.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
        testCategory.setDescription("Test Description");
        testCategory.setIconName("test-icon");
        testCategory.setNameKey("category.test_category");
    }

    @Test
    void getAllCategories_ShouldReturnSortedList() {
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findAll(any(Sort.class))).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository).findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Test
    void getCategoryById_WhenExists_ShouldReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        Category result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(testCategory.getName(), result.getName());
    }

    @Test
    void getCategoryById_WhenNotExists_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void getCategoryByName_WhenExists_ShouldReturnCategory() {
        when(categoryRepository.findByName("Test Category")).thenReturn(Optional.of(testCategory));

        Optional<Category> result = categoryService.getCategoryByName("Test Category");

        assertTrue(result.isPresent());
        assertEquals(testCategory.getName(), result.get().getName());
    }

    @Test
    void createCategory_WhenNameNotExists_ShouldCreateCategory() {
        when(categoryRepository.existsByName(testCategory.getName())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.createCategory(testCategory);

        assertNotNull(result);
        assertEquals(testCategory.getName(), result.getName());
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void createCategory_WhenNameExists_ShouldThrowException() {
        when(categoryRepository.existsByName(testCategory.getName())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(testCategory));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WhenValidUpdate_ShouldUpdateCategory() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Old Name");

        Category updatedDetails = new Category();
        updatedDetails.setName("New Name");
        updatedDetails.setDescription("New Description");
        updatedDetails.setIconName("new-icon");
        updatedDetails.setNameKey("category.new_name");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName("New Name")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedDetails);

        Category result = categoryService.updateCategory(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldRemoveAssociationsAndDelete() {
        Activity activity = new Activity();
        Set<Category> categories = new HashSet<>();
        categories.add(testCategory);
        activity.setCategories(categories);

        Set<Activity> activities = new HashSet<>();
        activities.add(activity);
        testCategory.setActivities(activities);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        categoryService.deleteCategory(1L);

        assertTrue(activity.getCategories().isEmpty());
        verify(categoryRepository).delete(testCategory);
    }

    @Test
    void initializeDefaultCategories_WhenEmpty_ShouldCreateDefaults() {
        when(categoryRepository.count()).thenReturn(0L);
        when(categoryRepository.existsByName(any())).thenReturn(false);

        categoryService.initializeDefaultCategories();

        // Verify that save was called 8 times (for each default category)
        verify(categoryRepository, times(8)).save(any(Category.class));
    }

    @Test
    void initializeDefaultCategories_WhenNotEmpty_ShouldNotCreateDefaults() {
        when(categoryRepository.count()).thenReturn(1L);

        categoryService.initializeDefaultCategories();

        verify(categoryRepository, never()).save(any(Category.class));
    }
}
