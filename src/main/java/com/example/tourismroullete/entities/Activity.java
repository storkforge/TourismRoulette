package com.example.tourismroullete.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @JsonProperty("activityName")
    private String name;

    @Column(length = 2000)
    @JsonProperty("activityDescription")
    private String description;

    private String location;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "activity_categories",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    // Additional non-persistent fields for JSON mapping
    @Transient
    @JsonProperty("latitude")
    private double latitude;

    @Transient
    @JsonProperty("longitude")
    private double longitude;

    // Constructor with full fields (if needed)
    public Activity(Long id, String name, String description, String location, Integer durationMinutes,
                    BigDecimal price, String imageUrl, Set<Category> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.imageUrl = imageUrl;
        this.categories = categories;
    }

    // Custom constructor to support RecommendationService (using Lombok for getters/setters)
    public Activity(String name, String description, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Helper methods for managing the bidirectional relationship
    public void addCategory(Category category) {
        this.categories.add(category);
        category.getActivities().add(this);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getActivities().remove(this);
    }

    // Equals and HashCode methods (excluding categories to prevent circular references)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Activity activity = (Activity) o;
        if (!Objects.equals(id, activity.id)) return false;
        return Objects.equals(name, activity.name);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    // ToString method (excluding categories to prevent circular references)
    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", durationMinutes=" + durationMinutes +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
