package com.campusstudyhub.repository;

import com.campusstudyhub.entity.LostFoundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LostFoundRepository extends JpaRepository<LostFoundItem, Long> {
    List<LostFoundItem> findAllByOrderByCreatedAtDesc();

    List<LostFoundItem> findByTypeOrderByCreatedAtDesc(String type);

    List<LostFoundItem> findByStatusOrderByCreatedAtDesc(String status);
}
