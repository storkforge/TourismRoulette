package com.example.tourismroullete.graphql;

import com.example.tourismroullete.entities.Activity;
import com.example.tourismroullete.entities.Category;
import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.repositories.ActivityRepository;
import com.example.tourismroullete.repositories.CategoryRepository;
import com.example.tourismroullete.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DataFetcher {

    private final ActivityRepository activityRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public DataFetcher(ActivityRepository activityRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @QueryMapping
    public List<Activity> activities() {
        return activityRepository.findAll();
    }

    @QueryMapping
    public List<Category> categories() {
        return categoryRepository.findAll();
    }

    @QueryMapping
    public List<User> users() {
        return userRepository.findAll();
    }

    @SchemaMapping(typeName = "Category", field = "activity")
    public List<Activity> activities(Category category) {
        return activityRepository.findAll().stream()
                .filter(activity -> activity.getCategories().contains(category))
                .toList();
    }

    @SchemaMapping(typeName = "Activity", field = "latitude")
    public Double getLatitude(Activity activity) {
        return activity.getLatitude();
    }

    @SchemaMapping(typeName = "Activity", field = "longitude")
    public Double getLongitude(Activity activity) {
        return activity.getLongitude();
    }

    @SchemaMapping(typeName = "Activity", field = "imageURL")
    public String getImageUrl(Activity activity) {
        return activity.getImageUrl();
    }
}
