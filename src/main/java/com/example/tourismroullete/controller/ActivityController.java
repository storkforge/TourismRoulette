package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.Activity;
import com.example.tourismroullete.service.ActivityService;
import com.example.tourismroullete.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        // Changed from "activities/list" to "list" to match your template location
        return "list";
    }

    @GetMapping("/activities/category/{categoryId}")
    public String listActivitiesByCategory(@PathVariable Long categoryId, Model model) {
        model.addAttribute("activities", activityService.getActivitiesByCategory(categoryId));
        model.addAttribute("category", categoryService.getCategoryById(categoryId));
        model.addAttribute("categories", categoryService.getAllCategories());
        // Changed from "activities/list" to "list" to match your template location
        return "list";
    }

    @GetMapping("/activities/{id}")
    public String viewActivity(@PathVariable Long id, Model model) {
        model.addAttribute("activity", activityService.getActivityById(id));
        // You'll need to create this template or adjust the return value
        return "activity-view";
    }

    @GetMapping("/admin/activities")
    public String adminActivities(Model model) {
        model.addAttribute("activities", activityService.getAllActivities());
        // You'll need to create this template or adjust the return value
        return "admin-activities";
    }

    @GetMapping("/admin/activities/new")
    public String newActivityForm(Model model) {
        model.addAttribute("activity", new Activity());
        model.addAttribute("categories", categoryService.getAllCategories());
        // You'll need to create this template or adjust the return value
        return "activity-form";
    }

    @GetMapping("/admin/activities/edit/{id}")
    public String editActivityForm(@PathVariable Long id, Model model) {
        model.addAttribute("activity", activityService.getActivityById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        // You'll need to create this template or adjust the return value
        return "activity-form";
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

    @PostMapping("/api/admin/activities")
    @ResponseBody
    public ResponseEntity<Activity> createActivity(
            @Valid @RequestBody Activity activity,
            @RequestParam(required = false) Set<Long> categoryIds) {
        Activity newActivity = activityService.createActivity(activity, categoryIds);
        return new ResponseEntity<>(newActivity, HttpStatus.CREATED);
    }

    @PutMapping("/api/admin/activities/{id}")
    @ResponseBody
    public ResponseEntity<Activity> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody Activity activity,
            @RequestParam(required = false) Set<Long> categoryIds) {
        Activity updatedActivity = activityService.updateActivity(id, activity, categoryIds);
        return ResponseEntity.ok(updatedActivity);
    }

    @DeleteMapping("/api/admin/activities/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/admin/activities/{activityId}/categories/{categoryId}")
    @ResponseBody
    public ResponseEntity<Activity> addCategoryToActivity(
            @PathVariable Long activityId,
            @PathVariable Long categoryId) {
        Activity activity = activityService.addCategoryToActivity(activityId, categoryId);
        return ResponseEntity.ok(activity);
    }

    @DeleteMapping("/api/admin/activities/{activityId}/categories/{categoryId}")
    @ResponseBody
    public ResponseEntity<Activity> removeCategoryFromActivity(
            @PathVariable Long activityId,
            @PathVariable Long categoryId) {
        Activity activity = activityService.removeCategoryFromActivity(activityId, categoryId);
        return ResponseEntity.ok(activity);
    }
}
