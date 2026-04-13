package com.example.userservice.client;

import com.example.userservice.dto.NotificationRequest;
import org.springframework.stereotype.Component;

/**
 * Fallback : si le "notification-service" est indisponible,
 * cette classe est appelée à la place (circuit breaker pattern).
 */
@Component
public class NotificationClientFallback implements NotificationClient {

    @Override
    public void sendNotification(NotificationRequest request) {
        System.out.println("[FALLBACK] Notification service is unavailable. Could not send notification to: "
                + request.getEmail());
    }
}
