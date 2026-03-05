package com.campusstudyhub.service;

import com.campusstudyhub.entity.AnalyticsEvent;
import com.campusstudyhub.repository.AnalyticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for tracking analytics events asynchronously.
 */
@Service
public class AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);
    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    /**
     * Tracks an event asynchronously.
     * 
     * @param eventType the type of event (e.g. "poi_view")
     * @param payload   a map of event details
     * @param userId    the user ID associated with the event (can be null)
     */
    @Async
    public void trackEvent(String eventType, Map<String, Object> payload, String userId) {
        try {
            AnalyticsEvent event = new AnalyticsEvent(eventType, payload, userId);
            analyticsRepository.save(event);
            log.debug("Tracked event: {} for user: {}", eventType, userId);
        } catch (Exception e) {
            log.error("Failed to track event: " + eventType, e);
        }
    }
}
