package com.campusstudyhub.controller;

import com.campusstudyhub.entity.Poi;
import com.campusstudyhub.repository.PoiRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CampusMapController {

    private final PoiRepository poiRepository;

    public CampusMapController(PoiRepository poiRepository) {
        this.poiRepository = poiRepository;
    }

    @GetMapping("/campus-map")
    public String campusMap(Model model) {
        List<Poi> pois = poiRepository.findAllByOrderByNameAsc();
        model.addAttribute("pois", pois);
        return "campus-map";
    }
}
