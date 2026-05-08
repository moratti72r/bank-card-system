package org.example.audit.auditor.controller;

import com.example.common.message.EventType;
import lombok.RequiredArgsConstructor;
import org.example.audit.auditor.entity.AuditLog;
import org.example.audit.auditor.security.OnlyAdmin;
import org.example.audit.auditor.service.AuditLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuditLogController {

    private final String dateTimePattern = "yyyy-MM-dd HH:mm:ss";

    private final AuditLogService auditLogService;

    @OnlyAdmin
    @QueryMapping
    public AuditLog auditRecord(@Argument Long id) {
        return auditLogService.getById(id);
    }

    @OnlyAdmin
    @QueryMapping
    public List<AuditLog> auditRecords(
            @Argument EventType eventType,
            @Argument String userId,
            @Argument String role,
            @Argument String methodArgs,
            @Argument String methodResult,
            @Argument @DateTimeFormat(pattern = dateTimePattern) LocalDateTime from,
            @Argument @DateTimeFormat(pattern = dateTimePattern) LocalDateTime to
    ) {
        return auditLogService.findFiltered(eventType, userId, role, methodArgs, methodResult, from, to);
    }

    @OnlyAdmin
    @QueryMapping
    public List<AuditLog> allAuditRecords() {
        return auditLogService.findFiltered(null, null, null, null, null, null, null);
    }
}
