package com.example.tourismroullete.model;

import lombok.Data;
import jakarta.persistence.*;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    private List<User> users; // Kanske måste vara en lista om du har flera användare per post

    private String title;

    @Column(length = 2000)
    private String content;

    private String location;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private com.example.tourismroullete.model.User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Like> likes;

    private LocalDateTime createdAt;

    private boolean isPublic = true;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters för alla fält om du inte använder Lombok
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
