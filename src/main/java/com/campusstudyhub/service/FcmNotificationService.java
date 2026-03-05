package com.campusstudyhub.service;

import com.campusstudyhub.entity.UserDeviceToken;
import com.campusstudyhub.repository.UserDeviceTokenRepository;
import com.campusstudyhub.repository.UserRepository;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of NotificationService using Firebase Cloud Messaging.
 */
@Service
@ConditionalOnProperty(name = "app.fcm.enabled", havingValue = "true")
public class FcmNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(FcmNotificationService.class);

    private final UserDeviceTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public FcmNotificationService(UserDeviceTokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void sendNotification(String message, String recipientEmail) {
        userRepository.findByEmail(recipientEmail).ifPresent(user -> {
            sendToUser(user.getId(), "Campus Study Hub", message);
        });
    }

    @Override
    public void sendToUser(Long userId, String title, String body) {
        List<UserDeviceToken> tokens = tokenRepository.findByUserId(userId);
        if (tokens.isEmpty()) {
            log.info("No FCM tokens found for user ID: {}. Skipping push notification.", userId);
            return;
        }

        List<String> registrationTokens = tokens.stream()
                .map(UserDeviceToken::getToken)
                .collect(Collectors.toList());

        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .addAllTokens(registrationTokens)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(multicastMessage);
            log.info("Successfully sent FCM notification to user ID {}. Success count: {}, Failure count: {}",
                    userId, response.getSuccessCount(), response.getFailureCount());

            // Update last used timestamp
            tokens.forEach(t -> t.setLastUsedAt(LocalDateTime.now()));
            tokenRepository.saveAll(tokens);

            // Cleanup invalid tokens if any
            if (response.getFailureCount() > 0) {
                handleFcmFailures(registrationTokens, response.getResponses());
            }
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM notification to user ID: " + userId, e);
        }
    }

    private void handleFcmFailures(List<String> tokens, List<SendResponse> responses) {
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
                MessagingErrorCode code = responses.get(i).getException().getMessagingErrorCode();
                if (code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT) {
                    log.info("Removing invalid/unregistered token: {} (Code: {})", tokens.get(i), code);
                    tokenRepository.deleteByToken(tokens.get(i));
                }
            }
        }
    }
}
