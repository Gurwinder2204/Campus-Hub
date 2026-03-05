package com.campusstudyhub.controller;

import com.campusstudyhub.entity.Complaint;
import com.campusstudyhub.repository.ComplaintRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/complaints")
public class ComplaintController {

    private final ComplaintRepository repository;

    public ComplaintController(ComplaintRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model, Principal principal) {
        model.addAttribute("complaints", repository.findAllByOrderByCreatedAtDesc());
        return "complaints/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("complaint", new Complaint());
        return "complaints/form";
    }

    @PostMapping
    public String create(@ModelAttribute Complaint complaint, Principal principal) {
        complaint.setSubmittedBy(principal.getName());
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus("OPEN");
        repository.save(complaint);
        return "redirect:/complaints";
    }

    @PostMapping("/{id}/respond")
    public String respond(@PathVariable Long id, @RequestParam String response) {
        repository.findById(id).ifPresent(c -> {
            c.setAdminResponse(response);
            c.setStatus("IN_PROGRESS");
            repository.save(c);
        });
        return "redirect:/complaints";
    }

    @PostMapping("/{id}/resolve")
    public String resolve(@PathVariable Long id) {
        repository.findById(id).ifPresent(c -> {
            c.setStatus("RESOLVED");
            c.setResolvedAt(LocalDateTime.now());
            repository.save(c);
        });
        return "redirect:/complaints";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/complaints";
    }
}
