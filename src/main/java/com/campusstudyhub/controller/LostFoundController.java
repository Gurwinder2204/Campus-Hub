package com.campusstudyhub.controller;

import com.campusstudyhub.entity.LostFoundItem;
import com.campusstudyhub.repository.LostFoundRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/lost-found")
public class LostFoundController {

    private final LostFoundRepository repository;

    public LostFoundController(LostFoundRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String type, Model model) {
        if (type != null && !type.isEmpty()) {
            model.addAttribute("items", repository.findByTypeOrderByCreatedAtDesc(type));
            model.addAttribute("filterType", type);
        } else {
            model.addAttribute("items", repository.findAllByOrderByCreatedAtDesc());
        }
        return "lostfound/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("item", new LostFoundItem());
        return "lostfound/form";
    }

    @PostMapping
    public String create(@ModelAttribute LostFoundItem item, Principal principal) {
        item.setPostedBy(principal.getName());
        item.setCreatedAt(LocalDateTime.now());
        item.setStatus("OPEN");
        repository.save(item);
        return "redirect:/lost-found";
    }

    @PostMapping("/{id}/resolve")
    public String resolve(@PathVariable Long id) {
        repository.findById(id).ifPresent(item -> {
            item.setStatus("RESOLVED");
            repository.save(item);
        });
        return "redirect:/lost-found";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/lost-found";
    }
}
