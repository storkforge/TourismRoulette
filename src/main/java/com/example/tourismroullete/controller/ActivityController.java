package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.Activity;
import com.example.tourismroullete.entities.Category;
import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.entities.UserCategory;
import com.example.tourismroullete.repositories.UserCategoryRepository;
import com.example.tourismroullete.repositories.UserRepository;
import com.example.tourismroullete.service.ActivityService;
import com.example.tourismroullete.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.tourismroullete.repositories.UserCategoryRepository;
import com.example.tourismroullete.repositories.UserRepository;

import java.util.*;
import java.util.stream.Collectors;


@Controller
public class ActivityController {

    private final ActivityService activityService;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final UserCategoryRepository userCategoryRepository;

    @Autowired
    public ActivityController(ActivityService activityService,
                              CategoryService categoryService,
                              UserRepository userRepository,
                              UserCategoryRepository userCategoryRepository) {
        this.activityService = activityService;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
        this.userCategoryRepository = userCategoryRepository;
    }

    // Web UI endpoints

    @GetMapping("/activities")
    public String listActivities(Model model, Authentication authentication) {
        List<Activity> activities = activityService.getAllActivities();
        List<Category> categories = categoryService.getAllCategories();

        List<Long> preferredCategoryIds = new ArrayList<>();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                Long userId = userOpt.get().getId();
                List<UserCategory> userCategories = userCategoryRepository.findByUserId(userId);
                preferredCategoryIds = userCategories.stream()
                        .map(uc -> uc.getCategory().getId())
                        .collect(Collectors.toList());
            }
        }

        model.addAttribute("activities", activities);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategories", Collections.emptyList());
        model.addAttribute("preferredCategoryIds", preferredCategoryIds); // 👈 add this

        return "activities";
    }

    @GetMapping("/activities/category/{categoryId}")
    public String listActivitiesByCategory(@PathVariable Long categoryId, Model model) {
        List<Activity> activities = activityService.getActivitiesByCategory(categoryId);
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("activities", activities);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategories", Collections.singletonList(categoryId));

        return "activities";
    }

    @GetMapping("/activities/search")
    public String searchActivities(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) List<Long> categoryIds,
            Model model) {

        List<Activity> activities;

        // Convert List to Set if needed for the service method
        Set<Long> categoryIdSet = categoryIds != null ? new HashSet<>(categoryIds) : null;

        if (term != null && !term.trim().isEmpty()) {
            if (categoryIdSet != null && !categoryIdSet.isEmpty()) {
                // Search by term and filter by categories
                activities = activityService.searchActivities(term, categoryIdSet);
            } else {
                // Search by term only
                activities = activityService.searchActivities(term, null);
            }
        } else if (categoryIdSet != null && !categoryIdSet.isEmpty()) {
            // Filter by categories only
            activities = activityService.getActivitiesByCategories(categoryIdSet, false);
        } else {
            // No filters applied, show all activities
            activities = activityService.getAllActivities();
        }

        // Add all necessary attributes to the model
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("activities", activities);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategories", categoryIds != null ? categoryIds : Collections.emptyList());

        return "activities";
    }

    @GetMapping("/activities/{id}")
    public String viewActivity(@PathVariable Long id, Model model) {
        Activity activity = activityService.getActivityById(id);

        String location = activity.getLocation();
        if (location != null && !location.isEmpty()) {
            String[] parts = location.split(", ");
            String latString = parts[0].split(": ")[1];
            String lonString = parts[1].split(": ")[1];

            try {
                double latitude = Double.parseDouble(latString);
                double longitude = Double.parseDouble(lonString);
                activity.setLatitude(latitude);
                activity.setLongitude(longitude);
                // Debugging: Print the latitude and longitude
                System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);
            } catch (NumberFormatException e) {
                System.out.println("Error parsing location: " + e.getMessage());
            }
        }

        model.addAttribute("activity", activity);
        return "activity-details";
    }


    @GetMapping("/activities/new")
    public String newActivityForm(Model model) {
        model.addAttribute("activity", new Activity());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "activity-form";
    }

    @GetMapping("/activities/edit/{id}")
    public String editActivityForm(@PathVariable Long id, Model model) {
        Activity activity = activityService.getActivityById(id);
        model.addAttribute("activity", activity);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "activity-form";
    }

    @PostMapping("/activities")
    public String createActivity(
            @ModelAttribute Activity activity,
            @RequestParam(required = false) Set<Long> categoryIds,
            Model model) {

        try {
            activityService.createActivity(activity, categoryIds);
            return "redirect:/activities";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating activity: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "activity-form";
        }
    }

    @PostMapping("/activities/{id}")
    public String updateActivity(
            @PathVariable Long id,
            @ModelAttribute Activity activity,
            @RequestParam(required = false) Set<Long> categoryIds,
            Model model) {

        try {
            activityService.updateActivity(id, activity, categoryIds);
            return "redirect:/activities";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating activity: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "activity-form";
        }
    }

    @PostMapping("/activities/delete/{id}")
    public String deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return "redirect:/activities";
    }

    // REST API endpoints

    @GetMapping("/api/activities")
    @ResponseBody
    public List<Activity> getAllActivitiesApi() {
        return activityService.getAllActivities();
    }

    @GetMapping("/api/activities/{id}")
    @ResponseBody
    public ResponseEntity<Activity> getActivityByIdApi(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/api/activities/filter")
    @ResponseBody
    public List<Activity> filterActivitiesApi(
            @RequestParam(required = false) Set<Long> categoryIds,
            @RequestParam(defaultValue = "false") boolean matchAll) {
        return activityService.getActivitiesByCategories(categoryIds, matchAll);
    }

    @GetMapping("/api/activities/search")
    @ResponseBody
    public List<Activity> searchActivitiesApi(
            @RequestParam String term,
            @RequestParam(required = false) Set<Long> categoryIds) {
        return activityService.searchActivities(term, categoryIds);
    }

}
