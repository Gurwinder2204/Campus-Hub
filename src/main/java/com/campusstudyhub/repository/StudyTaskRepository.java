package com.campusstudyhub.repository;

import com.campusstudyhub.entity.StudyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for StudyTask entity.
 */
@Repository
public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {

    List<StudyTask> findByUserIdOrderByDueDateAsc(Long userId);

    List<StudyTask> findByUserIdAndStatusOrderByDueDateAsc(Long userId, String status);

    long countByUserIdAndStatus(Long userId, String status);
}
