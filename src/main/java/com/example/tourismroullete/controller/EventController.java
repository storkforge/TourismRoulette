package com.example.tourismroullete.controller;

import com.example.tourismroullete.model.Event;
import com.example.tourismroullete.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String getUpcomingEvents(Model model) {
        List<Event> events = eventService.getUpcomingEvents();
        model.addAttribute("events", events);
        return "events"; // This should match the name of the Thymeleaf template (events.html)
    }

    @GetMapping("/create")
    public String createEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "create-event"; // Thymeleaf template for creating events
    }

    @PostMapping
    public String createEvent(@ModelAttribute Event event) {
        eventService.createEvent(event);
        return "redirect:/events"; // Redirect to the event list after creation
    }

    @PostMapping("/{eventId}/join")
    public String joinEvent(@PathVariable Long eventId) {
        // You can modify this logic to add a user to the event, if needed
        eventService.joinEvent(eventId, null); // Placeholder for user logic
        return "redirect:/events"; // Redirect back to the events list
    }
}
