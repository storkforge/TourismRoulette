
package com.example.tourismroullete.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "icon_name")
    private String iconName;

    @Column(name = "name_key")
    private String nameKey;

    @ManyToMany(mappedBy = "categories")
    private Set<Activity> activities = new HashSet<>();

    // Default constructor
    public Category() {
    }

    // Constructor with fields
    public Category(Long id, String name, String description, String iconName, String nameKey, Set<Activity> activities) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconName = iconName;
        this.nameKey = nameKey;
        this.activities = activities;
    }

    // Helper methods for managing the bidirectional relationship
    public void addActivity(Activity activity) {
        this.activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        this.activities.remove(activity);
    }

    // Equals and HashCode methods (excluding activities to prevent circular references)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (id != null ? !id.equals(category.id) : category.id != null) return false;
        return name != null ? name.equals(category.name) : category.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    // ToString method (excluding activities to prevent circular references)
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", iconName='" + iconName + '\'' +
                ", nameKey='" + nameKey + '\'' +
                '}';
    }

    @OneToMany(mappedBy = "category")
    private Set<UserCategory> userCategories;  // This relationship links the Category to Users

}
