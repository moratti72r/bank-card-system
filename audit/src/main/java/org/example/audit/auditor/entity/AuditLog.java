package org.example.audit.auditor.entity;

import com.example.common.message.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EventType eventType;
    
    private String userId;

    private String role;

    private String methodArgs;

    private String methodResult;

    private Boolean isSuccess;

    private LocalDateTime createdAt;
}
