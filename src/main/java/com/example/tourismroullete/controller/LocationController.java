package com.example.tourismroullete.controller;

import com.example.tourismroullete.entities.Activity;
import com.example.tourismroullete.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "*") // Tillåter frontend att anropa API:et
public class LocationController {

    @Autowired
    private RecommendationService recommendationService;


    @GetMapping("/recommendations")
    public ResponseEntity<List<Activity>> getRecommendations(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {

        // Set default values if lat and lon are null
        if (lat == null || lon == null) {
            lat = 59.3295;  // Default latitude (Stockholm)
            lon = 18.0687;  // Default longitude (Stockholm)
        }

        List<Activity> activities = recommendationService.getActivities(lat, lon);
        return ResponseEntity.ok(activities);
    }


}
