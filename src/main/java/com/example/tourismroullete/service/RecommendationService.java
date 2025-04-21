package com.example.tourismroullete.service;

import com.example.tourismroullete.Utils.distanceUtils;
import com.example.tourismroullete.entities.Activity;
import com.example.tourismroullete.repositories.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final double MAX_DISTANCE_KM = 5.0;

    private final ActivityRepository activityRepository;

    public RecommendationService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<Activity> getActivities(double lat, double lon) {
        List<Activity> activities = activityRepository.findAll(); // Fetch all activities from DB

        // Parse location and set latitude and longitude for each activity
        activities.forEach(this::parseLocationAndSetCoordinates);

        return activities.stream()
                .filter(activity -> distanceUtils.haversine(lat, lon, activity.getLatitude(), activity.getLongitude()) <= MAX_DISTANCE_KM)
                .collect(Collectors.toList());
    }

    private void parseLocationAndSetCoordinates(Activity activity) {
        if (activity.getLocation() != null && !activity.getLocation().isEmpty()) {
            String location = activity.getLocation();

            // Remove the "Lat:" and "Lon:" labels and trim spaces
            location = location.replace("Lat:", "").replace("Lon:", "").trim();

            // Split by comma to separate latitude and longitude
            String[] locationParts = location.split(",");
            if (locationParts.length == 2) {
                try {
                    String latStr = locationParts[0].trim();
                    String lonStr = locationParts[1].trim();

                    double latitude = Double.parseDouble(latStr);
                    double longitude = Double.parseDouble(lonStr);

                    activity.setLatitude(latitude);
                    activity.setLongitude(longitude);

                    System.out.println("Parsed Activity: " + activity.getName() + " - Latitude: " + latitude + ", Longitude: " + longitude);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing location: " + activity.getLocation());
                    // Handle the exception, e.g., log the error or set default coordinates
                }
            }
        }
    }
}
