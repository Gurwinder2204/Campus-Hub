package com.campusstudyhub.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Configuration to initialize Firebase Admin SDK.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${app.fcm.service-account-json-base64:}")
    private String serviceAccountBase64;

    @Value("${app.fcm.enabled:false}")
    private boolean fcmEnabled;

    @PostConstruct
    public void init() {
        if (!fcmEnabled) {
            log.info("FCM is disabled. Skipping Firebase initialization.");
            return;
        }

        try {
            if (serviceAccountBase64 == null || serviceAccountBase64.isEmpty()) {
                log.warn("FCM is enabled but service account JSON not provided. Push notifications will fail.");
                return;
            }

            byte[] decodedJson = Base64.getDecoder().decode(serviceAccountBase64);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(decodedJson)))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase has been initialized successfully.");
            }
        } catch (IOException | IllegalArgumentException e) {
            log.error("Error initializing Firebase Admin SDK", e);
        }
    }
}
