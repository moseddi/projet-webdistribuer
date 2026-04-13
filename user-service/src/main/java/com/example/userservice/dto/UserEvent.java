package com.example.userservice.dto;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent implements Serializable {
    private Long userId;
    private String email;
    private String eventType;
    private LocalDateTime timestamp;
}
