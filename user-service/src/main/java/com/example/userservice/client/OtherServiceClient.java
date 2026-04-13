package com.example.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Mock Service for synchronous communication
@FeignClient(name = "other-service")
public interface OtherServiceClient {

    @GetMapping("/api/other/info/{userId}")
    String getOtherInfo(@PathVariable("userId") Long userId);
}
