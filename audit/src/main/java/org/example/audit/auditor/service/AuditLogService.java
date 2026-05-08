package org.example.audit.auditor.service;

import com.example.common.message.EventMessage;
import com.example.common.message.EventType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.example.audit.auditor.entity.AuditLog;
import org.example.audit.auditor.repository.AuditLogRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void saveEvent(EventMessage event) {
        AuditLog auditLog = mapToAuditLog(event);
        auditLogRepository.save(auditLog);
    }

    public AuditLog getById(Long id) {
        return auditLogRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    public List<AuditLog> findFiltered(EventType eventType, String userId, String role, String methodArgs, String methodResult, LocalDateTime from, LocalDateTime to) {
        Specification<AuditLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (eventType != null) {
                predicates.add(cb.equal(root.get("eventType"), eventType));
            }
            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            if (methodArgs != null) {
                predicates.add(cb.like(root.get("methodArgs"), methodArgs));
            }
            if (methodResult != null) {
                predicates.add(cb.like(root.get("methodResult"), methodResult));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return auditLogRepository.findAll(spec);
    }

    private AuditLog mapToAuditLog(EventMessage message) {
        return AuditLog.builder().eventType(message.getEventType()).userId(message.getUserId()).role(message.getRole()).methodArgs(message.getMethodArgs() != null ? message.getMethodArgs().toString() : null).methodResult(message.getMethodResult() != null ? message.getMethodResult().toString() : null).isSuccess(message.getIsSuccess()).createdAt(message.getCreatedAt()).build();
    }

}
