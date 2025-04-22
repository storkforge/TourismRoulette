package com.example.tourismroullete.service;

import com.example.tourismroullete.Utils.distanceUtils;
import com.example.tourismroullete.entities.Activity;
import com.example.tourismroullete.repositories.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private ActivityRepository mockActivityRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private final double testLat = 40.7128;
    private final double testLon = -74.0060;
    private final double maxDistance = 5.0;

    private Activity activityNearby;
    private Activity activityFarAway;
    private Activity activityInvalidLocation;

    @BeforeEach
    void setUp() {
        activityNearby = new Activity();
        activityNearby.setName("Nearby Cafe");
        activityNearby.setLocation("Lat: 40.7100, Lon: -74.0000");

        activityFarAway = new Activity();
        activityFarAway.setName("Far Away Park");
        activityFarAway.setLocation("Lat: 34.0522, Lon: -118.2437");

        activityInvalidLocation = new Activity();
        activityInvalidLocation.setName("Place with Bad Location");
        activityInvalidLocation.setLocation("Lat: broken, Lon: format");
    }

    @Test
    void getActivities_shouldReturnOnlyNearbyActivities() {
        // Arrange
        when(mockActivityRepository.findAll()).thenReturn(Arrays.asList(activityNearby, activityFarAway));

        // Act
        List<Activity> result = recommendationService.getActivities(testLat, testLon);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(activityNearby));
        assertFalse(result.contains(activityFarAway));
        verify(mockActivityRepository, times(1)).findAll();
        assertEquals(40.7100, activityNearby.getLatitude(), 0.0001);
        assertEquals(-74.0000, activityNearby.getLongitude(), 0.0001);
    }

    @Test
    void getActivities_shouldReturnEmptyList_whenRepositoryIsEmpty() {
        // Arrange
        when(mockActivityRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Activity> result = recommendationService.getActivities(testLat, testLon);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockActivityRepository, times(1)).findAll();
    }

    @Test
    void getActivities_shouldHandleInvalidLocationFormatGracefully() {
        // Arrange
        when(mockActivityRepository.findAll()).thenReturn(Arrays.asList(activityNearby, activityInvalidLocation));

        // Act
        List<Activity> result = recommendationService.getActivities(testLat, testLon);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(activityNearby));
        assertFalse(result.contains(activityInvalidLocation));
        verify(mockActivityRepository, times(1)).findAll();
        assertEquals(0.0, activityInvalidLocation.getLatitude(), 0.0001);
        assertEquals(0.0, activityInvalidLocation.getLongitude(), 0.0001);
    }

    @Test
    void getActivities_usesDistanceUtilsCorrectly() {
        // Arrange
        when(mockActivityRepository.findAll()).thenReturn(List.of(activityNearby));

        try (MockedStatic<distanceUtils> mockedDistanceUtils = Mockito.mockStatic(distanceUtils.class)) {
            mockedDistanceUtils.when(() -> distanceUtils.haversine(eq(testLat), eq(testLon), eq(40.7100), eq(-74.0000)))
                    .thenReturn(maxDistance - 1.0);

            // Act
            List<Activity> result = recommendationService.getActivities(testLat, testLon);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            mockedDistanceUtils.verify(() -> distanceUtils.haversine(eq(testLat), eq(testLon), eq(40.7100), eq(-74.0000)));
        }
        verify(mockActivityRepository, times(1)).findAll();
    }
}
