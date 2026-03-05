package com.campusstudyhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity storing FCM registration tokens for users.
 * One user can have multiple devices/tokens.
 */
@Entity
@Table(name = "user_device_tokens", indexes = {
        @Index(name = "idx_token_user", columnList = "user_id"),
        @Index(name = "idx_token_value", columnList = "token", unique = true)
})
public class UserDeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt = LocalDateTime.now();

    public UserDeviceToken() {
    }

    public UserDeviceToken(Long userId, String token) {
        this.userId = userId;
        this.token = token;
        this.lastUsedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
}
