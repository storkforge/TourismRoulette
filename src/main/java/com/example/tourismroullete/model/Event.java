package com.example.tourismroullete.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import com.example.tourismroullete.entities.UserEnt;
import jakarta.persistence.*;

@Entity
@Table(name = "events") // Optional: Specifies the database table name
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDateTime eventDate;

    @ManyToOne
    private UserEnt organizer;

    @ManyToMany
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )

    private List<UserEnt> participants = new ArrayList<>(); //Users who are attending the event

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public UserEnt getOrganizer() {
        return organizer;
    }

    public void setOrganizer(UserEnt organizer) {
        this.organizer = organizer;
    }

    public List<UserEnt> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserEnt> participants) {
        this.participants = participants;
    }
}
