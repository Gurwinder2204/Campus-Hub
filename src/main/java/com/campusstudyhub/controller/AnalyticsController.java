package com.campusstudyhub.controller;

import com.campusstudyhub.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for tracking analytics events.
 */
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Endpoint to track a generic analytics event.
     */
    @PostMapping("/track")
    public ResponseEntity<Void> track(@RequestBody Map<String, Object> request, Authentication auth) {
        String eventType = (String) request.get("event_type");
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) request.get("payload");

        if (eventType == null || eventType.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String userId = (auth != null) ? auth.getName() : null;
        analyticsService.trackEvent(eventType, payload, userId);

        return ResponseEntity.accepted().build();
    }
}
