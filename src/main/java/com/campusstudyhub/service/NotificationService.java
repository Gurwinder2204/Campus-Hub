package com.campusstudyhub.service;

/**
 * Interface for sending push notifications.
 * Implementations can be FCM, logging-only, etc.
 */
public interface NotificationService {

    /**
     * Send a notification to all registered devices of a user.
     *
     * @param userId the target user ID
     * @param title  notification title
     * @param body   notification body text
     */
    void sendToUser(Long userId, String title, String body);

    /**
     * Send a general notification with a message to a specific recipient email.
     *
     * @param message        the notification message
     * @param recipientEmail the email address of the recipient
     */
    void sendNotification(String message, String recipientEmail);
}
