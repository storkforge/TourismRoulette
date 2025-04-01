package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.Activity;
import com.example.tourismroullete.service.ActivityService;
import com.example.tourismroullete.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
public class ActivityController {

    private final ActivityService activityService;
    private final CategoryService categoryService;

    @Autowired
    public ActivityController(ActivityService activityService, CategoryService categoryService) {
        this.activityService = activityService;
        this.categoryService = categoryService;
    }

    // Web UI endpoints

    @GetMapping("/activities")
    public String listActivities(Model model) {
        model.addAttribute("activities", activityService.getAllActivities());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "activities";
    }

    @GetMapping("/activities/category/{categoryId}")
    public String listActivitiesByCategory(@PathVariable Long categoryId, Model model) {
        model.addAttribute("activities", activityService.getActivitiesByCategory(categoryId));
        model.addAttribute("category", categoryService.getCategoryById(categoryId));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "activities";
    }

    @GetMapping("/activities/{id}")
    public String viewActivity(@PathVariable Long id, Model model) {
        model.addAttribute("activity", activityService.getActivityById(id));
        return "activity-view";
    }

    @GetMapping("/activities/new")
    public String newActivityForm(Model model) {
        model.addAttribute("activity", new Activity());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "activity-form";
    }

    @GetMapping("/activities/edit/{id}")
    public String editActivityForm(@PathVariable Long id, Model model) {
        model.addAttribute("activity", activityService.getActivityById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "activity-form";
    }

    // Form submission handlers

    @PostMapping("/activities")
    public String createActivity(
            @Valid @ModelAttribute("activity") Activity activity,
            BindingResult result,
            @RequestParam(required = false) Set<Long> categoryIds,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "activity-form";
        }

        if (categoryIds == null || categoryIds.isEmpty()) {
            model.addAttribute("categoryError", "Please select at least one category");
            model.addAttribute("categories", categoryService.getAllCategories());
            return "activity-form";
        }

        try {
            activityService.createActivity(activity, categoryIds);
            redirectAttributes.addFlashAttribute("successMessage", "Activity created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating activity: " + e.getMessage());
        }

        return "redirect:/activities";
    }

    @PostMapping("/activities/{id}")
    public String updateActivity(
            @PathVariable Long id,
            @Valid @ModelAttribute("activity") Activity activity,
            BindingResult result,
            @RequestParam(required = false) Set<Long> categoryIds,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "activity-form";
        }

        if (categoryIds == null || categoryIds.isEmpty()) {
            model.addAttribute("categoryError", "Please select at least one category");
            model.addAttribute("categories", categoryService.getAllCategories());
            return "activity-form";
        }

        try {
            activityService.updateActivity(id, activity, categoryIds);
            redirectAttributes.addFlashAttribute("successMessage", "Activity updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating activity: " + e.getMessage());
        }

        return "redirect:/activities";
    }

    @PostMapping("/activities/delete/{id}")
    public String deleteActivity(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            activityService.deleteActivity(id);
            redirectAttributes.addFlashAttribute("successMessage", "Activity deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting activity: " + e.getMessage());
        }
        return "redirect:/activities";
    }

    // REST API endpoints

    @GetMapping("/api/activities")
    @ResponseBody
    public List<Activity> getAllActivities() {
        return activityService.getAllActivities();
    }

    @GetMapping("/api/activities/{id}")
    @ResponseBody
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getActivityById(id));
    }

    @GetMapping("/api/activities/category/{categoryId}")
    @ResponseBody
    public List<Activity> getActivitiesByCategory(@PathVariable Long categoryId) {
        return activityService.getActivitiesByCategory(categoryId);
    }

    @GetMapping("/api/activities/filter")
    @ResponseBody
    public List<Activity> filterActivities(
            @RequestParam(required = false) Set<Long> categoryIds,
            @RequestParam(defaultValue = "false") boolean matchAll) {
        return activityService.getActivitiesByCategories(categoryIds, matchAll);
    }

    @GetMapping("/api/activities/search")
    @ResponseBody
    public List<Activity> searchActivities(
            @RequestParam String term,
            @RequestParam(required = false) Set<Long> categoryIds) {
        return activityService.searchActivities(term, categoryIds);
    }

    @PostMapping("/api/activities")
    @ResponseBody
    public ResponseEntity<Activity> createActivityApi(
            @Valid @RequestBody Activity activity,
            @RequestParam(required = false) Set<Long> categoryIds) {
        Activity newActivity = activityService.createActivity(activity, categoryIds);
        return new ResponseEntity<>(newActivity, HttpStatus.CREATED);
    }

    @PutMapping("/api/activities/{id}")
    @ResponseBody
    public ResponseEntity<Activity> updateActivityApi(
            @PathVariable Long id,
            @Valid @RequestBody Activity activity,
            @RequestParam(required = false) Set<Long> categoryIds) {
        Activity updatedActivity = activityService.updateActivity(id, activity, categoryIds);
        return ResponseEntity.ok(updatedActivity);
    }

    @DeleteMapping("/api/activities/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteActivityApi(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/activities/{activityId}/categories/{categoryId}")
    @ResponseBody
    public ResponseEntity<Activity> addCategoryToActivity(
            @PathVariable Long activityId,
            @PathVariable Long categoryId) {
        Activity activity = activityService.addCategoryToActivity(activityId, categoryId);
        return ResponseEntity.ok(activity);
    }

    @DeleteMapping("/api/activities/{activityId}/categories/{categoryId}")
    @ResponseBody
    public ResponseEntity<Activity> removeCategoryFromActivity(
            @PathVariable Long activityId,
            @PathVariable Long categoryId) {
        Activity activity = activityService.removeCategoryFromActivity(activityId, categoryId);
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/api/activities/random")
    @ResponseBody
    public ResponseEntity<Activity> getRandomActivity(
            @RequestParam(required = false) Set<Long> categoryIds) {
        List<Activity> activities;

        if (categoryIds != null && !categoryIds.isEmpty()) {
            activities = activityService.getActivitiesByCategories(categoryIds, false);
        } else {
            activities = activityService.getAllActivities();
        }

        if (activities.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Get a random activity from the list
        int randomIndex = (int) (Math.random() * activities.size());
        Activity randomActivity = activities.get(randomIndex);

        return ResponseEntity.ok(randomActivity);
    }
}
