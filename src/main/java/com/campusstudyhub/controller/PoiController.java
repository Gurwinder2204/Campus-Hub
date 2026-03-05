package com.campusstudyhub.controller;

import com.campusstudyhub.entity.Poi;
import com.campusstudyhub.repository.PoiRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/pois")
public class PoiController {

    private final PoiRepository repository;

    public PoiController(PoiRepository repository) {
        this.repository = repository;
    }

    // Web views
    @GetMapping
    public String list(@RequestParam(required = false) String category, Model model) {
        if (category != null && !category.isEmpty()) {
            model.addAttribute("pois", repository.findByCategoryOrderByNameAsc(category));
            model.addAttribute("filterCategory", category);
        } else {
            model.addAttribute("pois", repository.findAllByOrderByNameAsc());
        }
        return "pois/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("poi", new Poi());
        return "pois/form";
    }

    @PostMapping
    public String create(@ModelAttribute Poi poi) {
        poi.setCreatedAt(LocalDateTime.now());
        repository.save(poi);
        return "redirect:/pois";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/pois";
    }

    // REST API for mobile/AR app
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<Poi>> apiList(@RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(repository.findByCategoryOrderByNameAsc(category));
        }
        return ResponseEntity.ok(repository.findAllByOrderByNameAsc());
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Poi> apiGet(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
