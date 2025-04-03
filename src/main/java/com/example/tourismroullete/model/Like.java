package com.example.tourismroullete.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "post_likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
