package com.campusstudyhub.controller;

import com.campusstudyhub.entity.CampusEvent;
import com.campusstudyhub.repository.CampusEventRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/events")
public class EventController {

    private final CampusEventRepository repository;

    public EventController(CampusEventRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String category, Model model) {
        if (category != null && !category.isEmpty()) {
            model.addAttribute("events", repository.findByCategoryOrderByEventDateAsc(category));
            model.addAttribute("filterCategory", category);
        } else {
            model.addAttribute("events", repository.findAllByOrderByEventDateAsc());
        }
        return "events/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("event", new CampusEvent());
        return "events/form";
    }

    @PostMapping
    public String create(@ModelAttribute CampusEvent event, Principal principal) {
        event.setCreatedBy(principal.getName());
        event.setCreatedAt(LocalDateTime.now());
        repository.save(event);
        return "redirect:/events";
    }

    @PostMapping("/{id}/rsvp")
    public String rsvp(@PathVariable Long id) {
        repository.findById(id).ifPresent(e -> {
            e.setRsvpCount(e.getRsvpCount() + 1);
            repository.save(e);
        });
        return "redirect:/events";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/events";
    }
}
