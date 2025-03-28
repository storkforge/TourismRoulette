package com.example.tourismroullete.service;

import com.example.tourismroullete.entities.Activity;
import com.example.tourismroullete.entities.Category;
import com.example.tourismroullete.repositories.ActivityRepository;
import com.example.tourismroullete.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository, CategoryRepository categoryRepository) {
        this.activityRepository = activityRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Activity getActivityById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found with id: " + id));
    }

    public List<Activity> getActivitiesByCategory(Long categoryId) {
        return activityRepository.findByCategoryId(categoryId);
    }

    public List<Activity> getActivitiesByCategories(Set<Long> categoryIds, boolean matchAll) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return getAllActivities();
        }

        if (matchAll) {
            return activityRepository.findByAllCategoryIds(categoryIds, categoryIds.size());
        } else {
            return activityRepository.findByAnyCategoryIds(categoryIds);
        }
    }

    public List<Activity> searchActivities(String searchTerm, Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            // Implement basic search without category filtering if needed
            return getAllActivities(); // Replace with actual search logic
        }
        return activityRepository.searchByTermAndCategories(searchTerm, categoryIds);
    }

    @Transactional
    public Activity createActivity(Activity activity, Set<Long> categoryIds) {
        Set<Category> categories = new HashSet<>();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            categories = categoryIds.stream()
                    .map(id -> categoryRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id)))
                    .collect(Collectors.toSet());
        }
        activity.setCategories(categories);
        return activityRepository.save(activity);
    }

    @Transactional
    public Activity updateActivity(Long id, Activity activityDetails, Set<Long> categoryIds) {
        Activity activity = getActivityById(id);

        activity.setName(activityDetails.getName());
        activity.setDescription(activityDetails.getDescription());
        activity.setLocation(activityDetails.getLocation());
        activity.setDurationMinutes(activityDetails.getDurationMinutes());
        activity.setPrice(activityDetails.getPrice());
        activity.setImageUrl(activityDetails.getImageUrl());

        // Update categories if provided
        if (categoryIds != null) {
            Set<Category> categories = categoryIds.stream()
                    .map(catId -> categoryRepository.findById(catId)
                            .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + catId)))
                    .collect(Collectors.toSet());
            activity.getCategories().clear();
            activity.getCategories().addAll(categories);
        }

        return activityRepository.save(activity);
    }

    @Transactional
    public void deleteActivity(Long id) {
        Activity activity = getActivityById(id);
        activityRepository.delete(activity);
    }

    @Transactional
    public Activity addCategoryToActivity(Long activityId, Long categoryId) {
        Activity activity = getActivityById(activityId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));

        activity.addCategory(category);
        return activityRepository.save(activity);
    }

    @Transactional
    public Activity removeCategoryFromActivity(Long activityId, Long categoryId) {
        Activity activity = getActivityById(activityId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));

        activity.removeCategory(category);
        return activityRepository.save(activity);
    }
}
