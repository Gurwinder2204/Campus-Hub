package com.campusstudyhub.controller;

import com.campusstudyhub.entity.User;
import com.campusstudyhub.entity.UserDeviceToken;
import com.campusstudyhub.repository.UserDeviceTokenRepository;
import com.campusstudyhub.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller to handle notification-related operations like token registration.
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final UserDeviceTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public NotificationController(UserDeviceTokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/tokens")
    public ResponseEntity<Void> registerToken(@RequestParam String token, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        tokenRepository.findByToken(token).ifPresentOrElse(
                existing -> {
                    existing.setLastUsedAt(LocalDateTime.now());
                    tokenRepository.save(existing);
                },
                () -> {
                    UserDeviceToken newToken = new UserDeviceToken(user.getId(), token);
                    tokenRepository.save(newToken);
                });

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tokens")
    public ResponseEntity<Void> unregisterToken(@RequestParam String token) {
        tokenRepository.deleteByToken(token);
        return ResponseEntity.noContent().build();
    }
}
