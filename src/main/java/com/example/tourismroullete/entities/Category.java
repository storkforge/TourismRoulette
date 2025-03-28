package com.example.tourismroullete.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public void add(Activity activity) {
        this.activities.add(activity);
    }

    public void remove(Activity activity) {
        this.activities.remove(activity);
    }
}
