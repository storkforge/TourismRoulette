package com.example.tourismroullete.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Activity {
    private String activityName;
    private String activityDescription;
    private double latitude;
    private double longitude;

    public Activity(String activityName, String activityDescription, double latitude, double longitude) {
        this.activityName = activityName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters krävs av Jackson
    @JsonProperty("activityName")
    public String getName() {
        return activityName;
    }

    @JsonProperty("activityDescription")
    public String getDescription() {
        return activityDescription;
    }

    @JsonProperty("latitude")
    public double getLatitude() {
        return latitude;
    }

    @JsonProperty("longitude")
    public double getLongitude() {
        return longitude;
    }


}


