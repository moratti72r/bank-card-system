package com.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

        private EventType eventType;

        private UUID userId;

        private Map<String, Object> payload;

        @Builder.Default
        private Boolean isSuccess = true;

        @Builder.Default
        private LocalDateTime createdAt = LocalDateTime.now();
}
