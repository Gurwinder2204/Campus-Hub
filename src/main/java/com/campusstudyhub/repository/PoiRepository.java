package com.campusstudyhub.repository;

import com.campusstudyhub.entity.Poi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PoiRepository extends JpaRepository<Poi, Long> {
    List<Poi> findAllByOrderByNameAsc();

    List<Poi> findByCategoryOrderByNameAsc(String category);
}
