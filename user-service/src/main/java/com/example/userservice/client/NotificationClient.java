package com.example.userservice.client;

import com.example.userservice.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Communication SYNCHRONE via OpenFeign.
 * Ce client appelle le "notification-service" enregistré dans Eureka.
 * Le Load Balancer (lb://) est utilisé automatiquement via Eureka.
 */
@FeignClient(name = "notification-service", fallback = NotificationClientFallback.class)
public interface NotificationClient {

    @PostMapping("/api/notifications/send")
    void sendNotification(@RequestBody NotificationRequest request);
}
