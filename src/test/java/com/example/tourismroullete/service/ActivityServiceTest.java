package com.example.tourismroullete.service;

import com.example.tourismroullete.entities.Activity;
import com.example.tourismroullete.entities.Category;
import com.example.tourismroullete.repositories.ActivityRepository;
import com.example.tourismroullete.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ActivityService activityService;

    private Activity activity;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category = new Category();
        category.setId(1L);
        category.setName("Adventure");

        activity = new Activity();
        activity.setId(1L);
        activity.setName("Skydiving");
        activity.setDescription("A thrilling skydiving experience.");
        activity.setLocation("California");
        activity.setDurationMinutes(120);
        activity.setPrice(BigDecimal.valueOf(200.0));
        activity.setImageUrl("image_url");
        activity.setCategories(new HashSet<>(Collections.singletonList(category)));
    }

    @Test
    void testGetAllActivities() {
        List<Activity> activities = Arrays.asList(activity);
        when(activityRepository.findAll()).thenReturn(activities);

        List<Activity> result = activityService.getAllActivities();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Skydiving", result.get(0).getName());
    }

    @Test
    void testGetActivityById() {
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));

        Activity result = activityService.getActivityById(1L);
        assertNotNull(result);
        assertEquals("Skydiving", result.getName());
    }

    @Test
    void testGetActivityById_throwsException() {
        when(activityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> activityService.getActivityById(1L));
    }

    @Test
    void testCreateActivity() {
        Set<Long> categoryIds = new HashSet<>(Collections.singletonList(1L));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);

        Activity result = activityService.createActivity(activity, categoryIds);
        assertNotNull(result);
        assertEquals("Skydiving", result.getName());
        assertEquals(1, result.getCategories().size());
    }

    @Test
    void testUpdateActivity() {
        Set<Long> categoryIds = new HashSet<>(Collections.singletonList(1L));
        Activity updatedActivity = new Activity();
        updatedActivity.setName("Updated Skydiving");
        updatedActivity.setDescription("Updated Description");
        updatedActivity.setLocation("Updated Location");
        updatedActivity.setDurationMinutes(130);
        updatedActivity.setPrice(BigDecimal.valueOf(250.0));
        updatedActivity.setImageUrl("updated_image_url");

        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(activityRepository.save(any(Activity.class))).thenReturn(updatedActivity);

        Activity result = activityService.updateActivity(1L, updatedActivity, categoryIds);
        assertNotNull(result);
        assertEquals("Updated Skydiving", result.getName());
        assertEquals(130, result.getDurationMinutes());
    }

    @Test
    void testDeleteActivity() {
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));

        activityService.deleteActivity(1L);
        verify(activityRepository, times(1)).delete(activity);
    }

    @Test
    void testAddCategoryToActivity() {
        Set<Long> categoryIds = new HashSet<>(Collections.singletonList(1L));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);

        Activity result = activityService.addCategoryToActivity(1L, 1L);
        assertNotNull(result);
        assertTrue(result.getCategories().contains(category));
    }

    @Test
    void testRemoveCategoryFromActivity() {
        Set<Long> categoryIds = new HashSet<>(Collections.singletonList(1L));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);

        Activity result = activityService.removeCategoryFromActivity(1L, 1L);
        assertNotNull(result);
        assertFalse(result.getCategories().contains(category));
    }

    @Test
    void testSearchActivities() {
        String searchTerm = "Skydiving";
        Set<Long> categoryIds = new HashSet<>();
        categoryIds.add(1L);

        when(activityRepository.searchByTermAndCategories(searchTerm, categoryIds)).thenReturn(Collections.singletonList(activity));

        List<Activity> result = activityService.searchActivities(searchTerm, categoryIds);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Skydiving", result.get(0).getName());
    }
}
