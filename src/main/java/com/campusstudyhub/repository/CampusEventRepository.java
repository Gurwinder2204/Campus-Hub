package com.campusstudyhub.repository;

import com.campusstudyhub.entity.CampusEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CampusEventRepository extends JpaRepository<CampusEvent, Long> {
    List<CampusEvent> findAllByOrderByEventDateAsc();

    List<CampusEvent> findByCategoryOrderByEventDateAsc(String category);
}
