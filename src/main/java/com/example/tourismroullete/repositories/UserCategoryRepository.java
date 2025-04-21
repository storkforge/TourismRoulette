package com.example.tourismroullete.repositories;

import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.entities.UserCategory;
import com.example.tourismroullete.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {

    // Find all categories for a given user
    List<Category> findCategoriesByUserId(Long userId);

    // Delete all preferences by user ID (for clearing previous preferences)
    void deleteByUserId(Long userId);

    List<UserCategory> findByUserId(Long userId);
}
