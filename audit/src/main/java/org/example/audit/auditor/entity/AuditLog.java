package org.example.audit.auditor.entity;

import com.example.common.message.EventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

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

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> methodArgs;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> methodResult;

    private Boolean isSuccess;

    private LocalDateTime createdAt;
}
