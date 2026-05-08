package com.example.common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMessage {

        private EventType eventType;

        private String userId;

        private String role;

        private Map<String,Object> methodArgs;

        private Map<String, Object> methodResult;

        @Builder.Default
        private Boolean isSuccess = true;

        @Builder.Default
        private LocalDateTime createdAt = LocalDateTime.now();
}
