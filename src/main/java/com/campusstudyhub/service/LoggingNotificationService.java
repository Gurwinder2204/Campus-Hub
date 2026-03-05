package com.campusstudyhub.service;

import com.campusstudyhub.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Basic implementation of NotificationService that just logs messages.
 * Used in development/testing when FCM is not configured.
 */
@Service
@ConditionalOnProperty(name = "app.fcm.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationService.class);
    private final UserRepository userRepository;

    public LoggingNotificationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void sendNotification(String message, String recipientEmail) {
        log.info("NOTIFICATION to {}: {}", recipientEmail, message);
    }

    @Override
    public void sendToUser(Long userId, String title, String body) {
        if (userId == null) {
            log.warn("Cannot send notification to null user ID");
            return;
        }
        userRepository.findById(userId).ifPresentOrElse(
                user -> log.info("NOTIFICATION to user {} ({}): {} - {}",
                        userId, user.getEmail(), title, body),
                () -> log.info("NOTIFICATION to unknown user {}: {} - {}",
                        userId, title, body));
    }
}
