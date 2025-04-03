package com.example.tourismroullete.service;

import com.example.tourismroullete.entities.Activity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    // Method to fetch activities based on the lat and lon
    public List<Activity> getActivities(double lat, double lon) {
        // Creating a list of activities (static sample data)
        return List.of(
                new Activity("User Location", "Markera önskad plats", lat, lon),
                // Example Activity 1: Gamla Stan
                new Activity("Besök Ullevi", "Fotball", 57.705822, 11.987365)
        );
    }
}
