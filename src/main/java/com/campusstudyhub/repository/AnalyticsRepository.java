package com.campusstudyhub.repository;

import com.campusstudyhub.entity.AnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for AnalyticsEvent entity.
 */
@Repository
public interface AnalyticsRepository extends JpaRepository<AnalyticsEvent, Long> {
}
