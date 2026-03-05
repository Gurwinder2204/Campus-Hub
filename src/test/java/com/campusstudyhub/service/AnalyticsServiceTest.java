package com.campusstudyhub.service;

import com.campusstudyhub.entity.AnalyticsEvent;
import com.campusstudyhub.repository.AnalyticsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private AnalyticsRepository analyticsRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void testTrackEvent() {
        String eventType = "test_event";
        Map<String, Object> payload = Map.of("key", "value");
        String userId = "user@test.com";

        analyticsService.trackEvent(eventType, payload, userId);

        ArgumentCaptor<AnalyticsEvent> captor = ArgumentCaptor.forClass(AnalyticsEvent.class);
        verify(analyticsRepository, times(1)).save(captor.capture());

        AnalyticsEvent saved = captor.getValue();
        assertEquals(eventType, saved.getEventType());
        assertEquals(payload, saved.getPayload());
        assertEquals(userId, saved.getUserId());
    }

    @Test
    void testTrackEventHandlesException() {
        // Ensure service doesn't crash if repo fails (non-blocking)
        doThrow(new RuntimeException("DB down")).when(analyticsRepository).save(any());

        assertDoesNotThrow(() -> analyticsService.trackEvent("fail", Map.of(), null));
    }
}
